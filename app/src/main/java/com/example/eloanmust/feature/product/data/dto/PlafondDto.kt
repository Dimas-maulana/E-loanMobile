package com.example.eloanmust.feature.product.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Plafond DTO from API
 */
data class PlafondDto(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("minAmount")
    val minAmount: Double,
    
    @SerializedName("maxAmount")
    val maxAmount: Double,
    
    @SerializedName("interestRate")
    val interestRate: Double,
    
    @SerializedName("maxTenor")
    val maxTenor: Int,
    
    @SerializedName("isActive")
    val isActive: Boolean = true,
    
    @SerializedName("createdAt")
    val createdAt: String?
)
