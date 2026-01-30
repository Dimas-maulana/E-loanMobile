package com.example.eloanmust.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eloanmust.feature.loan.data.local.LoanEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Loan entity.
 * Supports offline-first loan history caching.
 */
@Dao
interface LoanDao {
    
    /**
     * Get all loans for current user, ordered by created date
     */
    @Query("SELECT * FROM loans WHERE userId = :userId ORDER BY createdAt DESC")
    fun getLoansByUserId(userId: Long): Flow<List<LoanEntity>>
    
    /**
     * Get all loans (sync)
     */
    @Query("SELECT * FROM loans WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getLoansByUserIdSync(userId: Long): List<LoanEntity>
    
    /**
     * Get loan by ID
     */
    @Query("SELECT * FROM loans WHERE id = :loanId LIMIT 1")
    fun getLoanById(loanId: Long): Flow<LoanEntity?>
    
    /**
     * Get loan by ID (sync)
     */
    @Query("SELECT * FROM loans WHERE id = :loanId LIMIT 1")
    suspend fun getLoanByIdSync(loanId: Long): LoanEntity?
    
    /**
     * Insert or update loan
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanEntity)
    
    /**
     * Insert multiple loans
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoans(loans: List<LoanEntity>)
    
    /**
     * Update loan status
     */
    @Query("UPDATE loans SET status = :status, updatedAt = :updatedAt WHERE id = :loanId")
    suspend fun updateLoanStatus(loanId: Long, status: String, updatedAt: Long)
    
    /**
     * Delete loan by ID
     */
    @Query("DELETE FROM loans WHERE id = :loanId")
    suspend fun deleteLoan(loanId: Long)
    
    /**
     * Clear all loans for user
     */
    @Query("DELETE FROM loans WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Long)
    
    /**
     * Clear all loans
     */
    @Query("DELETE FROM loans")
    suspend fun clearAll()
    
    /**
     * Get loans count by status
     */
    @Query("SELECT COUNT(*) FROM loans WHERE userId = :userId AND status = :status")
    suspend fun getLoansCountByStatus(userId: Long, status: String): Int
    
    /**
     * Get latest loan for user
     */
    @Query("SELECT * FROM loans WHERE userId = :userId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestLoan(userId: Long): LoanEntity?
}
