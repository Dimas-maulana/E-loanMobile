package com.example.eloanmust.feature.loan.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

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
    val totalInterest: Double,
    val totalPayment: Double,
    val monthlyInstallment: Double,
    val status: String,
    val purpose: String? = null,
    val plafondId: Long? = null,
    val plafondName: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val lastSyncedAt: Long = System.currentTimeMillis()
)
