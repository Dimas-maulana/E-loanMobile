package com.example.eloanmust.feature.loan.data.mapper

import com.example.eloanmust.feature.loan.data.dto.LoanApplicationRequest
import com.example.eloanmust.feature.loan.data.dto.LoanDto
import com.example.eloanmust.feature.loan.data.dto.LoanSimulationResponse
import com.example.eloanmust.feature.loan.data.local.LoanEntity
import com.example.eloanmust.feature.loan.data.local.PendingLoanEntity
import com.example.eloanmust.feature.loan.domain.model.Loan
import com.example.eloanmust.feature.loan.domain.model.LoanApplication
import com.example.eloanmust.feature.loan.domain.model.LoanSimulation
import com.example.eloanmust.feature.loan.domain.model.LoanStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Date format for parsing API date strings
 */
private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

/**
 * Parse date string to Date, returns current date if parsing fails
 */
private fun parseDate(dateString: String?): Date {
    return dateString?.let {
        try {
            dateFormat.parse(it) ?: Date()
        } catch (e: Exception) {
            Date()
        }
    } ?: Date()
}

/**
 * Extension function to convert LoanSimulationResponse to LoanSimulation domain model
 */
fun LoanSimulationResponse.toDomain(): LoanSimulation {
    return LoanSimulation(
        amount = this.loanAmount,
        tenor = this.tenor,
        interestRate = this.interestRate,
        monthlyPayment = this.monthlyInstallment,
        totalPayment = this.totalPayment,
        totalInterest = this.totalInterest,
        productName = this.plafondName,
        productId = this.plafondId
    )
}

/**
 * Extension function to convert LoanDto to Loan domain model
 */
fun LoanDto.toDomain(): Loan {
    return Loan(
        id = this.id,
        userId = 0L, // Will be set from token manager
        amount = this.amount,
        tenor = this.tenor,
        interestRate = this.interestRate,
        monthlyPayment = this.monthlyInstallment,
        totalPayment = this.totalPayment,
        status = LoanStatus.fromValue(this.status),
        productName = this.plafondName,
        productId = this.plafondId,
        purpose = this.purpose,
        notes = null,
        reviewedBy = null,
        reviewedAt = null,
        approvedBy = null,
        approvedAt = null,
        disbursedAt = null,
        rejectionReason = null,
        createdAt = parseDate(this.createdAt),
        updatedAt = this.updatedAt?.let { parseDate(it) }
    )
}

/**
 * Extension function to convert LoanEntity to Loan domain model
 */
fun LoanEntity.toDomain(): Loan {
    return Loan(
        id = this.id,
        userId = this.userId,
        amount = this.amount,
        tenor = this.tenor,
        interestRate = this.interestRate,
        monthlyPayment = this.monthlyInstallment,
        totalPayment = this.totalPayment,
        status = LoanStatus.fromValue(this.status),
        productName = this.plafondName,
        productId = this.plafondId,
        purpose = this.purpose,
        notes = null,
        reviewedBy = null,
        reviewedAt = null,
        approvedBy = null,
        approvedAt = null,
        disbursedAt = null,
        rejectionReason = null,
        createdAt = parseDate(this.createdAt),
        updatedAt = this.updatedAt?.let { parseDate(it) }
    )
}

/**
 * Extension function to convert LoanApplication to LoanApplicationRequest
 */
fun LoanApplication.toRequest(): LoanApplicationRequest {
    return LoanApplicationRequest(
        amount = this.amount,
        tenorMonth = this.tenor,
        latitude = this.latitude,
        longitude = this.longitude
    )
}

/**
 * Extension function to convert LoanDto to LoanEntity
 */
fun LoanDto.toEntity(userId: Long): LoanEntity {
    return LoanEntity(
        id = this.id,
        userId = userId,
        amount = this.amount,
        tenor = this.tenor,
        interestRate = this.interestRate,
        totalInterest = this.totalInterest,
        totalPayment = this.totalPayment,
        monthlyInstallment = this.monthlyInstallment,
        status = this.status,
        purpose = this.purpose,
        plafondId = this.plafondId,
        plafondName = this.plafondName,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        lastSyncedAt = System.currentTimeMillis()
    )
}

/**
 * Extension function to convert LoanEntity to LoanDto
 */
fun LoanEntity.toDto(): LoanDto {
    return LoanDto(
        id = this.id,
        amount = this.amount,
        tenor = this.tenor,
        interestRate = this.interestRate,
        totalInterest = this.totalInterest,
        totalPayment = this.totalPayment,
        monthlyInstallment = this.monthlyInstallment,
        status = this.status,
        purpose = this.purpose,
        plafondId = this.plafondId,
        plafondName = this.plafondName,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

/**
 * Extension function to convert list of LoanEntity to list of LoanDto
 */
fun List<LoanEntity>.toDtoList(): List<LoanDto> = map { it.toDto() }

/**
 * Extension function to convert list of LoanDto to list of LoanEntity
 */
fun List<LoanDto>.toEntityList(userId: Long): List<LoanEntity> = map { it.toEntity(userId) }

/**
 * Extension function to convert PendingLoanEntity to Loan domain model
 */
fun PendingLoanEntity.toDomain(): Loan {
    return Loan(
        id = -this.id, // Negative ID to indicate local pending item
        userId = this.userId,
        amount = this.amount,
        tenor = this.tenorMonth, // PendingLoanEntity stores tenorMonth
        interestRate = 0.0, // Not calculated yet
        monthlyPayment = 0.0,
        totalPayment = 0.0,
        status = LoanStatus.PENDING_REVIEW, // Display as Submitted/Pending
        productName = "Pending Sync...",
        productId = null,
        purpose = null,
        notes = "Waiting for internet connection...",
        reviewedBy = null,
        reviewedAt = null,
        approvedBy = null,
        approvedAt = null,
        disbursedAt = null,
        rejectionReason = null,
        createdAt = Date(this.createdAt),
        updatedAt = Date(this.createdAt)
    )
}
