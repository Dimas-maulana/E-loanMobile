package com.example.eloanmust.feature.profile.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Customer profile DTO from API
 */
data class CustomerProfileDto(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("userId")
    val userId: Long,
    
    @SerializedName("fullName")
    val fullName: String?,
    
    @SerializedName("nik")
    val nik: String?,
    
    @SerializedName("birthDate")
    val birthDate: String?,
    
    @SerializedName("birthPlace")
    val birthPlace: String?,
    
    @SerializedName("address")
    val address: String?,
    
    @SerializedName("phoneNumber")
    val phoneNumber: String?,
    
    @SerializedName("occupation")
    val occupation: String?,
    
    @SerializedName("monthlyIncome")
    val monthlyIncome: Double?,
    
    @SerializedName("ktpImageUrl")
    val ktpImageUrl: String?,
    
    @SerializedName("createdAt")
    val createdAt: String?,
    
    @SerializedName("updatedAt")
    val updatedAt: String?
)

/**
 * Profile update request DTO
 */
data class CustomerProfileRequest(
    @SerializedName("fullName")
    val fullName: String,
    
    @SerializedName("nik")
    val nik: String,
    
    @SerializedName("birthDate")
    val birthDate: String,
    
    @SerializedName("birthPlace")
    val birthPlace: String,
    
    @SerializedName("address")
    val address: String,
    
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    
    @SerializedName("occupation")
    val occupation: String,
    
    @SerializedName("monthlyIncome")
    val monthlyIncome: Double,
    
    @SerializedName("ktpImage")
    val ktpImage: String? = null // Base64 encoded
)

/**
 * Profile status response DTO
 */
data class ProfileStatusResponse(
    @SerializedName("isComplete")
    val isComplete: Boolean,
    
    @SerializedName("missingFields")
    val missingFields: List<String>?
)
