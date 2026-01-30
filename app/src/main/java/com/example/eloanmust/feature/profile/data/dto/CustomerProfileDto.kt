package com.example.eloanmust.feature.profile.data.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO for customer profile data
 */
data class CustomerProfileDto(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("userId")
    val userId: Long? = null,
    
    @SerializedName("fullName")
    val fullName: String? = null,
    
    @SerializedName("identityNumber")
    val nik: String? = null,

    @SerializedName("tanggalLahir")
    val birthDate: String? = null,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("bankAccountNumber")
    val bankAccountNumber: String? = null,

    @SerializedName("bankName")
    val bankName: String? = null,

    @SerializedName("bankAccountHolderName")
    val bankAccountName: String? = null,
    
    @SerializedName("ktpUrl")
    val ktpImageUrl: String? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

/**
 * DTO for profile update request
 */
data class CustomerProfileRequest(
    @SerializedName("fullName")
    val fullName: String,
    
    @SerializedName("identityNumber")
    val nik: String,

    @SerializedName("tanggalLahir")
    val birthDate: String,

    @SerializedName("address")
    val address: String,

    @SerializedName("bankAccountNumber")
    val bankAccountNumber: String? = null,

    @SerializedName("bankName")
    val bankName: String? = null,

    @SerializedName("bankAccountHolderName")
    val bankAccountName: String? = null
    
)

/**
 * DTO for profile status response
 */
data class ProfileStatusResponse(
    @SerializedName("isComplete", alternate = ["is_complete", "complete"])
    val isComplete: Boolean = false,
    
    @SerializedName("missingFields", alternate = ["missing_fields", "missing_data"])
    val missingFields: List<String>? = null
)
