package com.example.eloanmust.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eloanmust.feature.loan.data.local.PendingLoanEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for PendingLoan entity.
 */
@Dao
interface PendingLoanDao {
    
    /**
     * Insert pending loan
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pendingLoan: PendingLoanEntity): Long
    
    /**
     * Get all pending loans for user
     */
    @Query("SELECT * FROM pending_loans WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getAllPendingLoans(userId: Long): List<PendingLoanEntity>

    /**
     * Get ALL pending loans (system wide)
     */
    @Query("SELECT * FROM pending_loans ORDER BY createdAt DESC")
    suspend fun getAllPendingLoansSystem(): List<PendingLoanEntity>

    /**
     * Get all pending loans as Flow
     */
    @Query("SELECT * FROM pending_loans WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllPendingLoansFlow(userId: Long): Flow<List<PendingLoanEntity>>
    
    /**
     * Get pending loan by ID
     */
    @Query("SELECT * FROM pending_loans WHERE id = :id LIMIT 1")
    suspend fun getPendingLoanById(id: Long): PendingLoanEntity?
    
    /**
     * Delete pending loan by ID
     */
    @Query("DELETE FROM pending_loans WHERE id = :id")
    suspend fun deletePendingLoan(id: Long)
    
    /**
     * Clear all pending loans for user
     */
    @Query("DELETE FROM pending_loans WHERE userId = :userId")
    suspend fun clearPendingLoans(userId: Long)
}
