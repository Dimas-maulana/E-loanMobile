package com.example.eloanmust.feature.loan.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Loan DTO from API response
 */
data class LoanDto(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("user")
    val user: UserMinimalDto? = null,
    
    @SerializedName("userId")
    val userId: Long? = null,
    
    @SerializedName("amount")
    val amount: Double,
    
    @SerializedName("tenor")
    val tenor: Int,
    
    @SerializedName("interestRate")
    val interestRate: Double,
    
    @SerializedName("monthlyPayment")
    val monthlyPayment: Double,
    
    @SerializedName("totalPayment")
    val totalPayment: Double,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("plafond")
    val plafond: PlafondMinimalDto? = null,
    
    @SerializedName("purpose")
    val purpose: String? = null,
    
    @SerializedName("notes")
    val notes: String? = null,
    
    @SerializedName("reviewedBy")
    val reviewedBy: String? = null,
    
    @SerializedName("reviewedAt")
    val reviewedAt: String? = null,
    
    @SerializedName("approvedBy")
    val approvedBy: String? = null,
    
    @SerializedName("approvedAt")
    val approvedAt: String? = null,
    
    @SerializedName("disbursedAt")
    val disbursedAt: String? = null,
    
    @SerializedName("rejectionReason")
    val rejectionReason: String? = null,
    
    @SerializedName("createdAt")
    val createdAt: String,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

/**
 * Minimal user DTO
 */
data class UserMinimalDto(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("username")
    val username: String
)

/**
 * Minimal plafond DTO
 */
data class PlafondMinimalDto(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("name")
    val name: String
)

/**
 * Loan simulation request DTO
 */
data class LoanSimulationRequest(
    @SerializedName("amount")
    val amount: Double,
    
    @SerializedName("tenor")
    val tenor: Int
)

/**
 * Loan simulation response DTO
 */
data class LoanSimulationResponse(
    @SerializedName("amount")
    val amount: Double,
    
    @SerializedName("tenor")
    val tenor: Int,
    
    @SerializedName("interestRate")
    val interestRate: Double,
    
    @SerializedName("monthlyPayment")
    val monthlyPayment: Double,
    
    @SerializedName("totalPayment")
    val totalPayment: Double,
    
    @SerializedName("totalInterest")
    val totalInterest: Double,
    
    @SerializedName("plafond")
    val plafond: PlafondMinimalDto? = null
)

/**
 * Loan application request DTO
 */
data class LoanApplicationRequest(
    @SerializedName("amount")
    val amount: Double,
    
    @SerializedName("tenor")
    val tenor: Int,
    
    @SerializedName("purpose")
    val purpose: String? = null
)
