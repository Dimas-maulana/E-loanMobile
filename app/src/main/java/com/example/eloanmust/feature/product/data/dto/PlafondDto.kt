package com.example.eloanmust.feature.product.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Plafond DTO from API
 */
data class PlafondDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("name", alternate = ["productName", "nama", "plafondName"])
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("minAmount", alternate = ["min_amount"])
    val minAmount: Double,

    @SerializedName("maxAmount", alternate = ["max_amount"])
    val maxAmount: Double,

    @SerializedName("interestRate", alternate = ["interest_rate", "rate", "bunga"])
    val interestRate: Double,

    @SerializedName("maxTenor", alternate = ["max_tenor", "tenor_max", "tenorMonth"])
    val maxTenor: Int,

    @SerializedName("isActive")
    val isActive: Boolean = true,

    @SerializedName("createdAt")
    val createdAt: String?
)
