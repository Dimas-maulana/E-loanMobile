package com.example.eloanmust.feature.auth.data.datasource

import com.example.eloanmust.core.network.ApiResponse
import com.example.eloanmust.core.network.ApiService
import com.example.eloanmust.feature.auth.data.dto.ForgotPasswordRequest
import com.example.eloanmust.feature.auth.data.dto.GoogleAuthRequest
import com.example.eloanmust.feature.auth.data.dto.LoginRequest
import com.example.eloanmust.feature.auth.data.dto.LoginResponse
import com.example.eloanmust.feature.auth.data.dto.RegisterRequest
import com.example.eloanmust.feature.auth.data.dto.RegisterResponse
import com.example.eloanmust.feature.auth.data.dto.ResetPasswordRequest
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote data source for authentication operations.
 * Handles all network calls related to auth.
 */
@Singleton
class AuthRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {
    
    /**
     * Login user via API
     */
    suspend fun login(request: LoginRequest): Response<ApiResponse<LoginResponse>> {
        return apiService.login(request)
    }
    
    /**
     * Login with Google using Firebase ID Token
     */
    suspend fun loginWithGoogle(request: GoogleAuthRequest): Response<ApiResponse<LoginResponse>> {
        return apiService.loginWithGoogle(request)
    }
    
    /**
     * Register new user via API
     */
    suspend fun register(request: RegisterRequest): Response<ApiResponse<RegisterResponse>> {
        return apiService.register(request)
    }
    
    /**
     * Logout user via API
     */
    suspend fun logout(): Response<ApiResponse<Unit>> {
        return apiService.logout()
    }
    
    /**
     * Request password reset via API
     */
    suspend fun forgotPassword(request: ForgotPasswordRequest): Response<ApiResponse<Unit>> {
        return apiService.forgotPassword(request)
    }
    
    /**
     * Reset password via API
     */
    suspend fun resetPassword(request: ResetPasswordRequest): Response<ApiResponse<Unit>> {
        return apiService.resetPassword(request)
    }
}
