package com.example.eloanmust.feature.loan.data.datasource

import com.example.eloanmust.core.database.dao.LoanDao
import com.example.eloanmust.feature.loan.data.local.LoanEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local data source for loan operations.
 * Uses Room database for offline caching.
 */
@Singleton
class LoanLocalDataSource @Inject constructor(
    private val loanDao: LoanDao
) {
    /**
     * Get loans by user ID
     */
    fun getLoansByUserId(userId: Long): Flow<List<LoanEntity>> {
        return loanDao.getLoansByUserId(userId)
    }
    
    /**
     * Get loans by user ID (sync)
     */
    suspend fun getLoansByUserIdSync(userId: Long): List<LoanEntity> {
        return loanDao.getLoansByUserIdSync(userId)
    }
    
    /**
     * Get loan by ID
     */
    fun getLoanById(loanId: Long): Flow<LoanEntity?> {
        return loanDao.getLoanById(loanId)
    }
    
    /**
     * Get loan by ID (sync)
     */
    suspend fun getLoanByIdSync(loanId: Long): LoanEntity? {
        return loanDao.getLoanByIdSync(loanId)
    }
    
    /**
     * Insert or update loan
     */
    suspend fun insertLoan(loan: LoanEntity) {
        loanDao.insertLoan(loan)
    }
    
    /**
     * Insert multiple loans
     */
    suspend fun insertLoans(loans: List<LoanEntity>) {
        loanDao.insertLoans(loans)
    }
    
    /**
     * Update loan status
     */
    suspend fun updateLoanStatus(loanId: Long, status: String) {
        loanDao.updateLoanStatus(loanId, status, System.currentTimeMillis())
    }
    
    /**
     * Clear loans for user
     */
    suspend fun clearLoansForUser(userId: Long) {
        loanDao.clearLoansForUser(userId)
    }
    
    /**
     * Get loans count by status
     */
    suspend fun getLoansCountByStatus(userId: Long, status: String): Int {
        return loanDao.getLoansCountByStatus(userId, status)
    }
    
    /**
     * Get latest loan for user
     */
    suspend fun getLatestLoan(userId: Long): LoanEntity? {
        return loanDao.getLatestLoan(userId)
    }
}
