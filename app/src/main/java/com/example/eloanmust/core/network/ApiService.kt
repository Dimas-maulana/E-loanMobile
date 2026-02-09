package com.example.eloanmust.core.network

import com.example.eloanmust.feature.auth.data.dto.ForgotPasswordRequest
import com.example.eloanmust.feature.auth.data.dto.GoogleAuthRequest
import com.example.eloanmust.feature.auth.data.dto.LoginRequest
import com.example.eloanmust.feature.auth.data.dto.LoginResponse
import com.example.eloanmust.feature.auth.data.dto.RegisterRequest
import com.example.eloanmust.feature.auth.data.dto.RegisterResponse
import com.example.eloanmust.feature.auth.data.dto.ResetPasswordRequest
import com.example.eloanmust.feature.loan.data.dto.LoanApplicationRequest
import com.example.eloanmust.feature.loan.data.dto.LoanDto
import com.example.eloanmust.feature.loan.data.dto.LoanSimulationRequest
import com.example.eloanmust.feature.loan.data.dto.LoanSimulationResponse
import com.example.eloanmust.feature.notification.data.dto.NotificationDto
import com.example.eloanmust.feature.notification.data.dto.UnreadCountResponse
import com.example.eloanmust.feature.product.data.dto.PlafondDto
import com.example.eloanmust.feature.profile.data.dto.CustomerProfileDto
import com.example.eloanmust.feature.profile.data.dto.CustomerProfileRequest
import com.example.eloanmust.feature.profile.data.dto.ProfileStatusResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API Service interface defining all API endpoints.
 * All endpoints return Response<ApiResponse<T>> for proper error handling.
 */
interface ApiService {

    // ============================================
    // AUTHENTICATION ENDPOINTS
    // ============================================

    /**
     * Login user with credentials and FCM token
     */
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<LoginResponse>>

    /**
     * Login with Google using Firebase ID Token
     */
    @POST("api/auth/google")
    suspend fun loginWithGoogle(
        @Body request: GoogleAuthRequest
    ): Response<ApiResponse<LoginResponse>>

    /**
     * Register new user
     */
    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<RegisterResponse>>

    /**
     * Logout current user (invalidate token)
     */
    @POST("api/auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    /**
     * Request password reset
     */
    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<ApiResponse<Unit>>

    /**
     * Reset password with token
     */
    @POST("api/auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<ApiResponse<Unit>>

    // ============================================
    // PROFILE ENDPOINTS
    // ============================================

    /**
     * Get current user's profile
     */
    @GET("api/profile")
    suspend fun getProfile(): Response<ApiResponse<CustomerProfileDto>>

    /**
     * Update user profile (KYC)
     */
    @PUT("api/profile")
    suspend fun updateProfile(
        @Body request: CustomerProfileRequest
    ): Response<ApiResponse<CustomerProfileDto>>

    /**
     * Upload KTP image
     */
    @Multipart
    @POST("api/profile/upload-ktp")
    suspend fun uploadKtp(
        @Part file: MultipartBody.Part
    ): Response<ApiResponse<String>>

    /**
     * Get KTP image (Base64)
     */
    @GET("api/profile/ktp")
    suspend fun getKtpImage(): Response<ApiResponse<String>>

    /**
     * Check profile completion status
     */
    @GET("api/profile/status")
    suspend fun getProfileStatus(): Response<ApiResponse<ProfileStatusResponse>>

    // ============================================
    // PLAFOND (PRODUCT) ENDPOINTS
    // ============================================

    /**
     * Get all active plafonds
     */
    @GET("api/plafonds")
    suspend fun getPlafonds(): Response<ApiResponse<List<PlafondDto>>>

    /**
     * Get plafond by ID
     */
    @GET("api/plafonds/{id}")
    suspend fun getPlafondById(
        @Path("id") id: Long
    ): Response<ApiResponse<PlafondDto>>

    /**
     * Detect product by loan amount
     */
    @GET("api/plafonds/detect")
    suspend fun detectPlafond(
        @Query("amount") amount: Long
    ): Response<ApiResponse<PlafondDto>>

    // ============================================
    // LOAN ENDPOINTS
    // ============================================

    /**
     * Simulate loan (calculate monthly payment)
     */
    @POST("api/loans/simulate")
    suspend fun simulateLoan(
        @Body request: LoanSimulationRequest
    ): Response<ApiResponse<LoanSimulationResponse>>

    /**
     * Apply for a new loan
     */
    @POST("api/loans")
    suspend fun applyLoan(
        @Body request: LoanApplicationRequest
    ): Response<ApiResponse<LoanDto>>

    /**
     * Get current user's loans
     */
    @GET("api/loans")
    suspend fun getMyLoans(): Response<ApiResponse<List<LoanDto>>>

    /**
     * Get loan by ID
     */
    @GET("api/loans/{id}")
    suspend fun getLoanById(
        @Path("id") id: Long
    ): Response<ApiResponse<LoanDto>>

    // ============================================
    // NOTIFICATION ENDPOINTS
    // ============================================

    /**
     * Get all notifications for current user
     */
    @GET("api/notifications")
    suspend fun getNotifications(): Response<ApiResponse<List<NotificationDto>>>

    /**
     * Get unread notifications
     */
    @GET("api/notifications/unread")
    suspend fun getUnreadNotifications(): Response<ApiResponse<List<NotificationDto>>>

    /**
     * Get unread notification count
     */
    @GET("api/notifications/count")
    suspend fun getUnreadCount(): Response<ApiResponse<UnreadCountResponse>>

    /**
     * Mark notification as read
     */
    @PUT("api/notifications/{id}/read")
    suspend fun markNotificationAsRead(
        @Path("id") id: Long
    ): Response<ApiResponse<Unit>>

    /**
     * Mark all notifications as read
     */
    @PUT("api/notifications/read-all")
    suspend fun markAllNotificationsAsRead(): Response<ApiResponse<Unit>>
}
