package com.example.eloanmust.feature.loan.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Room entity for Loan.
 * Used for local caching in offline-first strategy.
 */
@Entity(tableName = "loans")
data class LoanEntity(
    @PrimaryKey
    val id: Long,
    val userId: Long,
    val amount: Double,
    val tenor: Int,
    val interestRate: Double,
    val monthlyPayment: Double,
    val totalPayment: Double,
    val status: String,
    val productName: String?,
    val productId: Long?,
    val purpose: String?,
    val notes: String?,
    val reviewedBy: String?,
    val reviewedAt: Long?,
    val approvedBy: String?,
    val approvedAt: Long?,
    val disbursedAt: Long?,
    val rejectionReason: String?,
    val createdAt: Long,
    val updatedAt: Long?,
    val cachedAt: Long = System.currentTimeMillis()
)
