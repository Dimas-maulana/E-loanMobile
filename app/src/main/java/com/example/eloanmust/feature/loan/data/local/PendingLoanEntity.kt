package com.example.eloanmust.feature.loan.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for Pending Loan.
 * Stores loan applications that were made offline and need to be synced.
 */
@Entity(tableName = "pending_loans")
data class PendingLoanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val tenor: Int,
    val tenorMonth: Int,
    val userId: Long,
    val createdAt: Long = System.currentTimeMillis()
)
