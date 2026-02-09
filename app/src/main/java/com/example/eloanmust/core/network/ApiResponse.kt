package com.example.eloanmust.core.network

import com.google.gson.annotations.SerializedName

/**
 * Generic API Response wrapper matching backend response format
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: T? = null,

    @SerializedName("error")
    val error: String? = null,

    @SerializedName("statusCode")
    val statusCode: Int? = null,

    @SerializedName("timestamp")
    val timestamp: String? = null
)

/**
 * Paginated response wrapper
 */
data class PaginatedResponse<T>(
    @SerializedName("content")
    val content: List<T> = emptyList(),

    @SerializedName("totalElements")
    val totalElements: Long = 0,

    @SerializedName("totalPages")
    val totalPages: Int = 0,

    @SerializedName("size")
    val size: Int = 0,

    @SerializedName("number")
    val number: Int = 0,

    @SerializedName("first")
    val first: Boolean = true,

    @SerializedName("last")
    val last: Boolean = true,

    @SerializedName("empty")
    val empty: Boolean = true
)

/**
 * Error response from API
 */
data class ErrorResponse(
    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("error")
    val error: String? = null,

    @SerializedName("statusCode")
    val statusCode: Int? = null,

    @SerializedName("path")
    val path: String? = null,

    @SerializedName("timestamp")
    val timestamp: String? = null
)
