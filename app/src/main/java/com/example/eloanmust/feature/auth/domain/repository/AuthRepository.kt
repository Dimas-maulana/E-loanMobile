package com.example.eloanmust.feature.auth.domain.repository

import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.feature.auth.domain.model.LoginCredentials
import com.example.eloanmust.feature.auth.domain.model.RegistrationData
import com.example.eloanmust.feature.auth.domain.model.User
import com.example.eloanmust.feature.auth.domain.model.UserSession
import kotlinx.coroutines.flow.Flow

/**
 * Auth Repository Interface.
 * Clean Architecture: Defined in domain layer, implemented in data layer.
 * No framework dependencies (Retrofit, Room) are referenced here.
 */
interface AuthRepository {
    
    /**
     * Login user with credentials
     * @param credentials Login credentials including FCM token
     * @return Resource containing UserSession on success
     */
    suspend fun login(credentials: LoginCredentials): Resource<UserSession>
    
    /**
     * Login with Google using Firebase ID Token
     * @param idToken Firebase ID Token from Google Sign-In
     * @param fcmToken Optional FCM token for push notifications
     * @return Resource containing UserSession on success
     */
    suspend fun loginWithGoogle(idToken: String, fcmToken: String?): Resource<UserSession>
    
    /**
     * Register new user
     * @param data Registration data
     * @return Resource containing User on success
     */
    suspend fun register(data: RegistrationData): Resource<User>
    
    /**
     * Logout current user
     * @return Resource<Unit> on success
     */
    suspend fun logout(): Resource<Unit>
    
    /**
     * Request password reset
     * @param email User's email
     * @return Resource<Unit> on success
     */
    suspend fun forgotPassword(email: String): Resource<Unit>
    
    /**
     * Reset password with token
     * @param token Reset token
     * @param newPassword New password
     * @return Resource<Unit> on success
     */
    suspend fun resetPassword(token: String, newPassword: String): Resource<Unit>
    
    /**
     * Check if user is logged in
     * @return Flow emitting login status
     */
    fun isLoggedIn(): Flow<Boolean>
    
    /**
     * Get current user ID
     * @return Flow emitting user ID or null
     */
    fun getCurrentUserId(): Flow<Long?>
    
    /**
     * Get current access token
     * @return Flow emitting access token or null
     */
    fun getAccessToken(): Flow<String?>
}
