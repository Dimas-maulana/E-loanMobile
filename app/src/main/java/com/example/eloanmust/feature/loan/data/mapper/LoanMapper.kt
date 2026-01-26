package com.example.eloanmust.feature.loan.data.mapper

import com.example.eloanmust.feature.loan.data.dto.LoanApplicationRequest
import com.example.eloanmust.feature.loan.data.dto.LoanDto
import com.example.eloanmust.feature.loan.data.dto.LoanSimulationRequest
import com.example.eloanmust.feature.loan.data.dto.LoanSimulationResponse
import com.example.eloanmust.feature.loan.data.local.LoanEntity
import com.example.eloanmust.feature.loan.domain.model.Loan
import com.example.eloanmust.feature.loan.domain.model.LoanApplication
import com.example.eloanmust.feature.loan.domain.model.LoanSimulation
import com.example.eloanmust.feature.loan.domain.model.LoanStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Mapper functions for Loan feature.
 */

// ============================================
// DTO TO DOMAIN MAPPERS
// ============================================

/**
 * Convert LoanDto to Loan domain model
 */
fun LoanDto.toDomain(): Loan {
    return Loan(
        id = this.id,
        userId = this.user?.id ?: this.userId ?: 0L,
        amount = this.amount,
        tenor = this.tenor,
        interestRate = this.interestRate,
        monthlyPayment = this.monthlyPayment,
        totalPayment = this.totalPayment,
        status = LoanStatus.fromValue(this.status),
        productName = this.plafond?.name,
        productId = this.plafond?.id,
        purpose = this.purpose,
        notes = this.notes,
        reviewedBy = this.reviewedBy,
        reviewedAt = this.reviewedAt?.toDate(),
        approvedBy = this.approvedBy,
        approvedAt = this.approvedAt?.toDate(),
        disbursedAt = this.disbursedAt?.toDate(),
        rejectionReason = this.rejectionReason,
        createdAt = this.createdAt.toDate() ?: Date(),
        updatedAt = this.updatedAt?.toDate()
    )
}

/**
 * Convert LoanSimulationResponse to LoanSimulation domain model
 */
fun LoanSimulationResponse.toDomain(): LoanSimulation {
    return LoanSimulation(
        amount = this.amount,
        tenor = this.tenor,
        interestRate = this.interestRate,
        monthlyPayment = this.monthlyPayment,
        totalPayment = this.totalPayment,
        totalInterest = this.totalInterest,
        productName = this.plafond?.name,
        productId = this.plafond?.id
    )
}

// ============================================
// DOMAIN TO DTO MAPPERS
// ============================================

/**
 * Convert LoanApplication to LoanApplicationRequest
 */
fun LoanApplication.toRequest(): LoanApplicationRequest {
    return LoanApplicationRequest(
        amount = this.amount,
        tenor = this.tenor,
        purpose = this.purpose
    )
}

/**
 * Create simulation request
 */
fun createSimulationRequest(amount: Double, tenor: Int): LoanSimulationRequest {
    return LoanSimulationRequest(
        amount = amount,
        tenor = tenor
    )
}

// ============================================
// DTO TO ENTITY MAPPERS
// ============================================

/**
 * Convert LoanDto to LoanEntity for local caching
 */
fun LoanDto.toEntity(): LoanEntity {
    return LoanEntity(
        id = this.id,
        userId = this.user?.id ?: this.userId ?: 0L,
        amount = this.amount,
        tenor = this.tenor,
        interestRate = this.interestRate,
        monthlyPayment = this.monthlyPayment,
        totalPayment = this.totalPayment,
        status = this.status,
        productName = this.plafond?.name,
        productId = this.plafond?.id,
        purpose = this.purpose,
        notes = this.notes,
        reviewedBy = this.reviewedBy,
        reviewedAt = this.reviewedAt?.toDate()?.time,
        approvedBy = this.approvedBy,
        approvedAt = this.approvedAt?.toDate()?.time,
        disbursedAt = this.disbursedAt?.toDate()?.time,
        rejectionReason = this.rejectionReason,
        createdAt = this.createdAt.toDate()?.time ?: System.currentTimeMillis(),
        updatedAt = this.updatedAt?.toDate()?.time,
        cachedAt = System.currentTimeMillis()
    )
}

// ============================================
// ENTITY TO DOMAIN MAPPERS
// ============================================

/**
 * Convert LoanEntity to Loan domain model
 */
fun LoanEntity.toDomain(): Loan {
    return Loan(
        id = this.id,
        userId = this.userId,
        amount = this.amount,
        tenor = this.tenor,
        interestRate = this.interestRate,
        monthlyPayment = this.monthlyPayment,
        totalPayment = this.totalPayment,
        status = LoanStatus.fromValue(this.status),
        productName = this.productName,
        productId = this.productId,
        purpose = this.purpose,
        notes = this.notes,
        reviewedBy = this.reviewedBy,
        reviewedAt = this.reviewedAt?.let { Date(it) },
        approvedBy = this.approvedBy,
        approvedAt = this.approvedAt?.let { Date(it) },
        disbursedAt = this.disbursedAt?.let { Date(it) },
        rejectionReason = this.rejectionReason,
        createdAt = Date(this.createdAt),
        updatedAt = this.updatedAt?.let { Date(it) }
    )
}

// ============================================
// HELPER FUNCTIONS
// ============================================

/**
 * Parse date string to Date object
 */
private fun String.toDate(): Date? {
    return try {
        val formats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd"
        )
        
        for (format in formats) {
            try {
                return SimpleDateFormat(format, Locale.getDefault()).parse(this)
            } catch (e: Exception) {
                continue
            }
        }
        null
    } catch (e: Exception) {
        null
    }
}
