package com.example.eloanmust.feature.loan.domain.model

import java.util.Date

/**
 * Domain model for Loan entity.
 */
data class Loan(
    val id: Long,
    val userId: Long,
    val amount: Double,
    val tenor: Int,
    val interestRate: Double,
    val monthlyPayment: Double,
    val totalPayment: Double,
    val status: LoanStatus,
    val productName: String?,
    val productId: Long?,
    val purpose: String?,
    val notes: String?,
    val reviewedBy: String?,
    val reviewedAt: Date?,
    val approvedBy: String?,
    val approvedAt: Date?,
    val disbursedAt: Date?,
    val rejectionReason: String?,
    val createdAt: Date,
    val updatedAt: Date?
)

/**
 * Enum for loan status
 */
enum class LoanStatus(val value: String, val displayName: String) {
    PENDING_REVIEW("PENDING_REVIEW", "Menunggu Review"),
    REVIEWED("REVIEWED", "Sedang Ditinjau"),
    APPROVED("APPROVED", "Disetujui"),
    REJECTED("REJECTED", "Ditolak"),
    DISBURSED("DISBURSED", "Dana Cair");
    
    companion object {
        fun fromValue(value: String): LoanStatus {
            return entries.find { it.value == value } ?: PENDING_REVIEW
        }
    }
}

/**
 * Domain model for loan simulation result
 */
data class LoanSimulation(
    val amount: Double,
    val tenor: Int,
    val interestRate: Double,
    val monthlyPayment: Double,
    val totalPayment: Double,
    val totalInterest: Double,
    val productName: String?,
    val productId: Long?
)

/**
 * Domain model for loan application request
 */
data class LoanApplication(
    val amount: Double,
    val tenor: Int,
    val purpose: String?,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
