package com.example.eloanmust.feature.loan.data.repository

import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.core.datastore.TokenManager
import com.example.eloanmust.core.network.safeApiCall
import com.example.eloanmust.feature.loan.data.datasource.LoanLocalDataSource
import com.example.eloanmust.feature.loan.data.datasource.LoanRemoteDataSource
import com.example.eloanmust.feature.loan.data.dto.LoanSimulationRequest
import com.example.eloanmust.feature.loan.data.mapper.toDomain
import com.example.eloanmust.feature.loan.data.mapper.toEntity
import com.example.eloanmust.feature.loan.data.mapper.toRequest
import com.example.eloanmust.feature.loan.domain.model.Loan
import com.example.eloanmust.feature.loan.domain.model.LoanApplication
import com.example.eloanmust.feature.loan.domain.model.LoanSimulation
import com.example.eloanmust.feature.loan.domain.repository.LoanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of LoanRepository with offline-first strategy.
 *
 * Offline-First Flow:
 * 1. Emit cached data from local database immediately
 * 2. Fetch fresh data from remote API
 * 3. Update local cache with remote data
 * 4. Emit updated data
 */
@Singleton
class LoanRepositoryImpl @Inject constructor(
    private val remoteDataSource: LoanRemoteDataSource,
    private val localDataSource: LoanLocalDataSource,
    private val tokenManager: TokenManager,
    private val networkMonitor: com.example.eloanmust.core.network.NetworkMonitor,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) : LoanRepository {

    override suspend fun simulateLoan(amount: Double, tenor: Int): Resource<LoanSimulation> {
        Timber.d("Simulating loan: amount=$amount, tenor=$tenor")

        val result = safeApiCall {
            remoteDataSource.simulateLoan(LoanSimulationRequest(amount, tenor))
        }

        return when (result) {
            is Resource.Success -> {
                Timber.d("Loan simulation successful")
                Resource.Success(result.data.toDomain())
            }
            is Resource.Error -> {
                Timber.e("Loan simulation failed: ${result.message}")
                Resource.Error(result.message, result.code, result.exception)
            }
            is Resource.Loading -> Resource.Loading
            is Resource.Idle -> Resource.Idle
        }
    }

    override suspend fun applyLoan(application: LoanApplication): Resource<Loan> {
        Timber.d("Applying for loan: amount=${application.amount}, tenor=${application.tenor}")

        val userId = tokenManager.userId.first() ?: return Resource.Error("User not logged in")

        // Check internet connection
        val isOnline = networkMonitor.isCurrentlyConnected()

        if (!isOnline) {
            Timber.d("Offline mode: Saving loan application to pending queue")

            // Create pending loan entity
            val pendingLoan = com.example.eloanmust.feature.loan.data.local.PendingLoanEntity(
                amount = application.amount,
                tenor = application.tenor, // In domain model this is actually months
                tenorMonth = application.tenor,
                userId = userId
            )

            // Save to local database
            localDataSource.insertPendingLoan(pendingLoan)

            // Schedule background sync
            scheduleLoanSync()

            // Return success with pending status
            return Resource.Success(pendingLoan.toDomain())
        }

        val result = safeApiCall {
            remoteDataSource.applyLoan(application.toRequest())
        }

        return when (result) {
            is Resource.Success -> {
                // Cache the new loan locally
                val loan = result.data.toDomain()
                localDataSource.insertLoan(result.data.toEntity(userId))

                Timber.d("Loan application successful: id=${loan.id}")
                Resource.Success(loan)
            }
            is Resource.Error -> {
                Timber.e("Loan application failed: ${result.message}")
                Resource.Error(result.message, result.code, result.exception)
            }
            is Resource.Loading -> Resource.Loading
            is Resource.Idle -> Resource.Idle
        }
    }

    private fun scheduleLoanSync() {
        val workRequest = androidx.work.OneTimeWorkRequest.Builder(com.example.eloanmust.core.worker.LoanSyncWorker::class.java)
            .setConstraints(
                androidx.work.Constraints.Builder()
                    .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                    .build()
            )
            .build()

        androidx.work.WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "loan_sync_work",
                androidx.work.ExistingWorkPolicy.KEEP,
                workRequest
            )
    }

    /**
     * Offline-first implementation for getting user's loans.
     * 1. First emit cached data (if available) + Pending loans
     * 2. Fetch from remote
     * 3. Update cache
     * 4. Emit updated data + Pending loans
     */
    override fun getMyLoans(): Flow<Resource<List<Loan>>> = flow {
        val userId = tokenManager.userId.first() ?: run {
            emit(Resource.Error("User not logged in"))
            return@flow
        }

        Timber.d("Getting loans for user: $userId")

        // Helper to get combined list
        suspend fun getCombinedLoans(): List<Loan> {
            val cachedLoans = localDataSource.getLoansByUserIdSync(userId).map { it.toDomain() }
            val pendingLoans = localDataSource.getPendingLoansByUserId(userId).map { it.toDomain() }
            // Show pending, then cached
            return pendingLoans + cachedLoans
        }

        // Step 1: Emit cached data first (if available)
        val combinedLoans = getCombinedLoans()
        if (combinedLoans.isNotEmpty()) {
            Timber.d("Emitting ${combinedLoans.size} loans (cached + pending)")
            emit(Resource.Success(combinedLoans))
        } else {
            // Emit loading if no cache
            emit(Resource.Loading)
        }

        // Step 2: Fetch from remote
        Timber.d("Fetching loans from remote...")

        // Only fetch if online
        if (networkMonitor.isCurrentlyConnected()) {
            val remoteResult = safeApiCall {
                remoteDataSource.getMyLoans()
            }

            when (remoteResult) {
                is Resource.Success -> {
                    // Step 3: Update local cache
                    val remoteLoans = remoteResult.data
                    localDataSource.clearLoansForUser(userId)
                    localDataSource.insertLoans(remoteLoans.map { it.toEntity(userId) })

                    // Step 4: Emit fresh data + pending
                    val freshCombinedLoans = getCombinedLoans()
                    Timber.d("Emitting ${freshCombinedLoans.size} fresh loans from remote (+ pending)")
                    emit(Resource.Success(freshCombinedLoans))
                }
                is Resource.Error -> {
                    // If we have cached data, keep showing it but log error
                    if (combinedLoans.isNotEmpty()) {
                        Timber.w("Remote fetch failed, using cached data: ${remoteResult.message}")
                        // Don't emit error since we have cached data
                    } else {
                        Timber.e("Remote fetch failed, no cache available: ${remoteResult.message}")
                        emit(Resource.Error(remoteResult.message, remoteResult.code, remoteResult.exception))
                    }
                }
                is Resource.Loading -> {
                    // Already emitted loading if no cache
                }
                is Resource.Idle -> {
                    // Do nothing
                }
            }
        } else {
            Timber.d("Offline, skipping remote fetch")
        }
    }

    /**
     * Offline-first implementation for getting loan by ID.
     */
    override fun getLoanById(loanId: Long): Flow<Resource<Loan>> = flow {
        Timber.d("Getting loan by ID: $loanId")

        val userId = tokenManager.userId.first() ?: run {
            emit(Resource.Error("User not logged in"))
            return@flow
        }

        // Step 1: Check cache first
        val cachedLoan = localDataSource.getLoanByIdSync(loanId)
        if (cachedLoan != null) {
            Timber.d("Emitting cached loan: $loanId")
            emit(Resource.Success(cachedLoan.toDomain()))
        } else {
            emit(Resource.Loading)
        }

        // Step 2: Fetch from remote for fresh data
        val remoteResult = safeApiCall {
            remoteDataSource.getLoanById(loanId)
        }

        when (remoteResult) {
            is Resource.Success -> {
                // Update cache
                localDataSource.insertLoan(remoteResult.data.toEntity(userId))

                // Emit fresh data
                Timber.d("Emitting fresh loan from remote: $loanId")
                emit(Resource.Success(remoteResult.data.toDomain()))
            }
            is Resource.Error -> {
                if (cachedLoan == null) {
                    Timber.e("Loan fetch failed: ${remoteResult.message}")
                    emit(Resource.Error(remoteResult.message, remoteResult.code, remoteResult.exception))
                } else {
                    Timber.w("Remote fetch failed, using cached loan: ${remoteResult.message}")
                }
            }
            is Resource.Loading -> {}
            is Resource.Idle -> {}
        }
    }

    override suspend fun refreshLoans(): Resource<Unit> {
        val userId = tokenManager.userId.first() ?: return Resource.Error("User not logged in")

        Timber.d("Refreshing loans for user: $userId")

        val result = safeApiCall {
            remoteDataSource.getMyLoans()
        }

        return when (result) {
            is Resource.Success -> {
                localDataSource.clearLoansForUser(userId)
                localDataSource.insertLoans(result.data.map { it.toEntity(userId) })
                Timber.d("Loans refreshed: ${result.data.size} items")
                Resource.Success(Unit)
            }
            is Resource.Error -> {
                Timber.e("Loans refresh failed: ${result.message}")
                Resource.Error(result.message, result.code, result.exception)
            }
            is Resource.Loading -> Resource.Loading
            is Resource.Idle -> Resource.Idle
        }
    }

    override suspend fun getLoansCountByStatus(status: String): Int {
        val userId = tokenManager.userId.first() ?: return 0
        return localDataSource.getLoansCountByStatus(userId, status)
    }
}
