package com.example.eloanmust.feature.auth.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Register request DTO
 */
data class RegisterRequest(
    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("fullname")
    val fullname: String,

    @SerializedName("phone")
    val phone: String
)

/**
 * Register response DTO
 */
data class RegisterResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("message")
    val message: String? = null
)

/**
 * Forgot password request DTO
 */
data class ForgotPasswordRequest(
    @SerializedName("email")
    val email: String
)

/**
 * Reset password request DTO
 */
data class ResetPasswordRequest(
    @SerializedName("token")
    val token: String,

    @SerializedName("newPassword")
    val newPassword: String,

    @SerializedName("confirmPassword")
    val confirmPassword: String
)
