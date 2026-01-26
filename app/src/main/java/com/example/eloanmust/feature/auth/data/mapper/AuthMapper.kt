package com.example.eloanmust.feature.auth.data.mapper

import com.example.eloanmust.feature.auth.data.dto.LoginRequest
import com.example.eloanmust.feature.auth.data.dto.LoginResponse
import com.example.eloanmust.feature.auth.data.dto.RegisterRequest
import com.example.eloanmust.feature.auth.data.dto.RegisterResponse
import com.example.eloanmust.feature.auth.data.dto.UserDto
import com.example.eloanmust.feature.auth.domain.model.LoginCredentials
import com.example.eloanmust.feature.auth.domain.model.RegistrationData
import com.example.eloanmust.feature.auth.domain.model.User
import com.example.eloanmust.feature.auth.domain.model.UserSession

/**
 * Mapper functions for Auth feature.
 * Converts between DTOs and Domain models.
 */

// ============================================
// LOGIN MAPPERS
// ============================================

/**
 * Convert LoginCredentials domain model to LoginRequest DTO
 */
fun LoginCredentials.toRequest(): LoginRequest {
    return LoginRequest(
        username = this.username,
        password = this.password,
        fcmToken = this.fcmToken
    )
}

/**
 * Convert LoginResponse DTO to UserSession domain model
 */
fun LoginResponse.toUserSession(): UserSession {
    return UserSession(
        user = this.user?.toDomain() ?: User(
            id = 0,
            username = "",
            email = "",
            role = "USER"
        ),
        accessToken = this.accessToken,
        refreshToken = this.refreshToken,
        expiresAt = this.expiresIn?.let { System.currentTimeMillis() + (it * 1000) }
    )
}

// ============================================
// USER MAPPERS
// ============================================

/**
 * Convert UserDto to User domain model
 */
fun UserDto.toDomain(): User {
    return User(
        id = this.id,
        username = this.username,
        email = this.email,
        role = this.role?.name ?: "USER",
        isActive = this.isActive
    )
}

// ============================================
// REGISTRATION MAPPERS
// ============================================

/**
 * Convert RegistrationData domain model to RegisterRequest DTO
 */
fun RegistrationData.toRequest(): RegisterRequest {
    return RegisterRequest(
        username = this.username,
        email = this.email,
        password = this.password
    )
}

/**
 * Convert RegisterResponse DTO to User domain model
 */
fun RegisterResponse.toDomain(): User {
    return User(
        id = this.id,
        username = this.username,
        email = this.email,
        role = "USER",
        isActive = true
    )
}
