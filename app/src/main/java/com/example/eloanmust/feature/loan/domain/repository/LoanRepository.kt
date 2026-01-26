package com.example.eloanmust.feature.loan.domain.repository

import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.feature.loan.domain.model.Loan
import com.example.eloanmust.feature.loan.domain.model.LoanApplication
import com.example.eloanmust.feature.loan.domain.model.LoanSimulation
import kotlinx.coroutines.flow.Flow

/**
 * Loan Repository Interface.
 * Supports offline-first strategy with local caching.
 */
interface LoanRepository {
    
    /**
     * Simulate loan calculation
     * @param amount Loan amount
     * @param tenor Loan tenor in months
     * @return Resource containing LoanSimulation
     */
    suspend fun simulateLoan(amount: Double, tenor: Int): Resource<LoanSimulation>
    
    /**
     * Apply for a new loan
     * @param application Loan application data
     * @return Resource containing created Loan
     */
    suspend fun applyLoan(application: LoanApplication): Resource<Loan>
    
    /**
     * Get current user's loans
     * Implements offline-first: emits cached data first, then fetches from remote
     * @return Flow of Resource containing list of loans
     */
    fun getMyLoans(): Flow<Resource<List<Loan>>>
    
    /**
     * Get loan by ID
     * @param loanId Loan ID
     * @return Flow of Resource containing Loan
     */
    fun getLoanById(loanId: Long): Flow<Resource<Loan>>
    
    /**
     * Refresh loans from remote
     * @return Resource<Unit> on success
     */
    suspend fun refreshLoans(): Resource<Unit>
    
    /**
     * Get cached loans count by status
     * @param status Loan status
     * @return Number of loans with given status
     */
    suspend fun getLoansCountByStatus(status: String): Int
}
