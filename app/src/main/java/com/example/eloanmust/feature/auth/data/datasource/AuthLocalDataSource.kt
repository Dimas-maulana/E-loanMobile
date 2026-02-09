package com.example.eloanmust.feature.auth.data.datasource

import com.example.eloanmust.core.datastore.TokenManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local data source for authentication.
 * Handles token and session storage using DataStore.
 */
@Singleton
class AuthLocalDataSource @Inject constructor(
    private val tokenManager: TokenManager
) {

    /**
     * Save login session data
     */
    suspend fun saveLoginSession(
        accessToken: String,
        refreshToken: String?,
        userId: Long,
        username: String,
        email: String,
        role: String
    ) {
        tokenManager.saveLoginData(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userId,
            username = username,
            email = email,
            role = role
        )
    }

    /**
     * Clear login session (logout)
     */
    suspend fun clearLoginSession() {
        tokenManager.clearLoginData()
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Flow<Boolean> {
        return tokenManager.isLoggedIn
    }

    /**
     * Get current user ID
     */
    fun getCurrentUserId(): Flow<Long?> {
        return tokenManager.userId
    }

    /**
     * Get access token
     */
    fun getAccessToken(): Flow<String?> {
        return tokenManager.accessToken
    }

    /**
     * Get username
     */
    fun getUsername(): Flow<String?> {
        return tokenManager.username
    }

    /**
     * Get user email
     */
    fun getUserEmail(): Flow<String?> {
        return tokenManager.userEmail
    }

    /**
     * Get user role
     */
    fun getUserRole(): Flow<String?> {
        return tokenManager.userRole
    }
}
