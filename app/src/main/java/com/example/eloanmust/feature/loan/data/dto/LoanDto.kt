package com.example.eloanmust.feature.loan.data.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO for loan simulation request
 */
data class LoanSimulationRequest(
    @SerializedName("amount")
    val amount: Double,
    
    @SerializedName("tenor")
    val tenor: Int,
    
    @SerializedName("plafondId")
    val plafondId: Long? = null
)

/**
 * DTO for loan simulation response
 */
data class LoanSimulationResponse(
    @SerializedName("loanAmount", alternate = ["amount", "loan_amount", "pokok"])
    val loanAmount: Double = 0.0,
    
    @SerializedName("tenor")
    val tenor: Int = 0,
    
    @SerializedName("interestRate", alternate = ["interest_rate", "rate", "bunga", "bunga_persen"])
    val interestRate: Double = 0.0,
    
    @SerializedName("totalInterest", alternate = ["total_interest", "total_bunga", "bunga_total"])
    val totalInterest: Double = 0.0,
    
    @SerializedName("totalPayment", alternate = ["total_payment", "total_bayar"])
    val totalPayment: Double = 0.0,
    
    @SerializedName("monthlyInstallment", alternate = ["monthly_installment", "installment", "cicilan_per_bulan", "cicilan"])
    val monthlyInstallment: Double = 0.0,
    
    @SerializedName("plafondId", alternate = ["plafond_id"])
    val plafondId: Long? = null,
    
    @SerializedName("plafondName", alternate = ["plafond_name", "product_name"])
    val plafondName: String? = null
)

/**
 * DTO for loan application request - matches swagger POST api/loans
 * Only requires amount and tenorMonth
 */
data class LoanApplicationRequest(
    @SerializedName("amount")
    val amount: Double,
    
    @SerializedName("tenorMonth")
    val tenorMonth: Int
)

/**
 * DTO for loan data - matches swagger response from POST api/loans
 */
data class LoanDto(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("customerId")
    val customerId: Long? = null,
    
    @SerializedName("customerName")
    val customerName: String? = null,
    
    @SerializedName("plafondId")
    val plafondId: Long? = null,
    
    @SerializedName("plafondName")
    val plafondName: String? = null,
    
    @SerializedName("amount")
    val amount: Double,
    
    @SerializedName("tenorMonth")
    val tenorMonth: Int? = null,
    
    @SerializedName("tenor")
    val tenor: Int = 0,
    
    @SerializedName("maxTenorMonth")
    val maxTenorMonth: Int? = null,
    
    @SerializedName("baseInterestRate")
    val baseInterestRate: Double? = null,
    
    @SerializedName("actualInterestRate")
    val actualInterestRate: Double? = null,
    
    @SerializedName("interestRate")
    val interestRate: Double = 0.0,
    
    @SerializedName("totalInterest")
    val totalInterest: Double = 0.0,
    
    @SerializedName("totalPayment")
    val totalPayment: Double = 0.0,
    
    @SerializedName("monthlyInstallment")
    val monthlyInstallment: Double = 0.0,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("purpose")
    val purpose: String? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
) {
    // Helper to get effective tenor (tenorMonth or tenor)
    fun getEffectiveTenor(): Int = tenorMonth ?: tenor
    
    // Helper to get effective interest rate (actualInterestRate or interestRate)
    fun getEffectiveInterestRate(): Double = actualInterestRate ?: interestRate
}
