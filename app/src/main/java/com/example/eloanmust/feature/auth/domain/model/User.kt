package com.example.eloanmust.feature.auth.domain.model

/**
 * Domain model for User entity.
 * Clean Architecture: This model is used in the domain layer
 * and is free from any framework dependencies.
 */
data class User(
    val id: Long,
    val username: String,
    val email: String,
    val role: String,
    val isActive: Boolean = true
)

/**
 * Domain model for authenticated user session
 */
data class UserSession(
    val user: User,
    val accessToken: String,
    val refreshToken: String? = null,
    val expiresAt: Long? = null
)

/**
 * Domain model for login credentials
 */
data class LoginCredentials(
    val username: String,
    val password: String,
    val fcmToken: String
)

/**
 * Domain model for registration data
 */
data class RegistrationData(
    val username: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)
