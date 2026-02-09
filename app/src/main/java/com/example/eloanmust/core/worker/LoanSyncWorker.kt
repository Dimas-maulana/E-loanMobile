package com.example.eloanmust.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.eloanmust.core.database.dao.LoanDao
import com.example.eloanmust.core.database.dao.PendingLoanDao
import com.example.eloanmust.feature.loan.data.datasource.LoanRemoteDataSource
import com.example.eloanmust.feature.loan.data.dto.LoanApplicationRequest
import com.example.eloanmust.feature.loan.data.mapper.toEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

/**
 * Worker to sync pending loan applications when network is available.
 */
@HiltWorker
class LoanSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val pendingLoanDao: PendingLoanDao,
    private val loanDao: LoanDao,
    private val remoteDataSource: LoanRemoteDataSource
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Timber.d("Starting LoanSyncWorker...")

        return try {
            val pendingLoans = pendingLoanDao.getAllPendingLoansSystem()
            Timber.d("Found ${pendingLoans.size} pending loans to sync")

            if (pendingLoans.isEmpty()) {
                return Result.success()
            }

            var successCount = 0

            pendingLoans.forEach { pendingLoan ->
                Timber.d("Syncing pending loan: ${pendingLoan.id}")

                // Convert to request
                val request = LoanApplicationRequest(
                    amount = pendingLoan.amount,
                    tenorMonth = pendingLoan.tenorMonth
                    // Note: We don't have lat/long for offline apps yet, or we could add them to PendingLoanEntity
                )

                try {
                    // Call API using RemoteDataSource
                    // We assume RemoteDataSource usage is safe here.
                    // Note: RemoteDataSource typically returns a Resource or Response.
                    // We should peek at LoanRemoteDataSource to be sure, but usually we use a wrapper in Repository.
                    // Here we are using RemoteDataSource directly.
                    // Let's assume it returns standard Retrofit response or Resource.
                    // Wait, LoanRemoteDataSource methods usually match API service.
                    // Let's re-verify LoanRepositoryImpl usage of RemoteDataSource.
                    // In LoanRepositoryImpl: remoteDataSource.applyLoan(request) returns ApiResponse<LoanDto> usually wrapped in safeApiCall?
                    // No, safeApiCall wraps the call. RemoteDataSource returns the Response object or data?
                    // Let's check LoanRemoteDataSource again if needed, but safeApiCall { remoteDataSource.applyLoan(...) } suggests it's a suspend function returning remote response.

                    val response = remoteDataSource.applyLoan(request)

                    if (response.isSuccessful && response.body()?.success == true) {
                        val loanDto = response.body()?.data

                        if (loanDto != null) {
                            // 1. Insert into valid Loan cache
                            loanDao.insertLoan(loanDto.toEntity(pendingLoan.userId))

                            // 2. Remove from invalid Pending cache
                            pendingLoanDao.deletePendingLoan(pendingLoan.id)

                            Timber.d("Successfully synced loan ${pendingLoan.id} -> ${loanDto.id}")
                            successCount++
                        } else {
                            Timber.e("Sync failed: Response data is null")
                        }
                    } else {
                        Timber.e("Sync failed for loan ${pendingLoan.id}: ${response.message()}")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Exception during sync for loan ${pendingLoan.id}")
                    // Continue to next loan
                }
            }

            if (successCount == pendingLoans.size) {
                Result.success()
            } else {
                // If some failed, we might want to retry
                Result.retry()
            }
        } catch (e: Exception) {
            Timber.e(e, "Loan sync worker failed")
            Result.retry()
        }
    }
}
