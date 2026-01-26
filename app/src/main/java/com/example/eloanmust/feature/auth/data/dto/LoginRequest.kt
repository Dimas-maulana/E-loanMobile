package com.example.eloanmust.feature.auth.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Login request DTO - includes FCM token as required
 */
data class LoginRequest(
    @SerializedName("username")
    val username: String,
    
    @SerializedName("password")
    val password: String,
    
    @SerializedName("fcmToken")
    val fcmToken: String
)

/**
 * Login response DTO
 */
data class LoginResponse(
    @SerializedName("accessToken")
    val accessToken: String,
    
    @SerializedName("refreshToken")
    val refreshToken: String? = null,
    
    @SerializedName("tokenType")
    val tokenType: String = "Bearer",
    
    @SerializedName("expiresIn")
    val expiresIn: Long? = null,
    
    @SerializedName("user")
    val user: UserDto? = null
)

/**
 * User DTO from API response
 */
data class UserDto(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("username")
    val username: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("role")
    val role: RoleDto? = null,
    
    @SerializedName("isActive")
    val isActive: Boolean = true,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

/**
 * Role DTO
 */
data class RoleDto(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String? = null
)
