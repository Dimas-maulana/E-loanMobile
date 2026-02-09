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
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response

/**
 * Unit tests for AuthRemoteDataSource
 */
class AuthRemoteDataSourceTest {

    @Mock
    private lateinit var apiService: ApiService

    private lateinit var authRemoteDataSource: AuthRemoteDataSource

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        authRemoteDataSource = AuthRemoteDataSource(apiService)
    }

    // ============================================
    // LOGIN TESTS
    // ============================================

    @Test
    fun `login success - returns successful response`() = runTest {
        // Given
        val request = LoginRequest(
            username = "testuser",
            password = "password123",
            fcmToken = "fcm_token_123"
        )
        val loginResponse = LoginResponse(
            accessToken = "access_token_123",
            tokenType = "Bearer"
        )
        val apiResponse = ApiResponse(
            success = true,
            message = "Login successful",
            data = loginResponse
        )
        val expectedResponse = Response.success(apiResponse)

        `when`(apiService.login(request)).thenReturn(expectedResponse)

        // When
        val result = authRemoteDataSource.login(request)

        // Then
        verify(apiService).login(request)
        assertTrue(result.isSuccessful)
        assertEquals(apiResponse, result.body())
    }

    @Test
    fun `login failure - returns error response`() = runTest {
        // Given
        val request = LoginRequest(
            username = "wronguser",
            password = "wrongpassword",
            fcmToken = "fcm_token_123"
        )
        val errorResponse: Response<ApiResponse<LoginResponse>> = Response.error(
            401,
            """{"success":false,"message":"Invalid credentials"}""".toResponseBody()
        )

        `when`(apiService.login(request)).thenReturn(errorResponse)

        // When
        val result = authRemoteDataSource.login(request)

        // Then
        verify(apiService).login(request)
        assertEquals(401, result.code())
    }

    // ============================================
    // LOGIN WITH GOOGLE TESTS
    // ============================================

    @Test
    fun `loginWithGoogle success - returns successful response`() = runTest {
        // Given
        val request = GoogleAuthRequest(idToken = "google_id_token_123")
        val loginResponse = LoginResponse(
            accessToken = "access_token_456",
            tokenType = "Bearer"
        )
        val apiResponse = ApiResponse(
            success = true,
            message = "Google login successful",
            data = loginResponse
        )
        val expectedResponse = Response.success(apiResponse)

        `when`(apiService.loginWithGoogle(request)).thenReturn(expectedResponse)

        // When
        val result = authRemoteDataSource.loginWithGoogle(request)

        // Then
        verify(apiService).loginWithGoogle(request)
        assertTrue(result.isSuccessful)
        assertEquals(apiResponse, result.body())
    }

    @Test
    fun `loginWithGoogle failure - returns error response`() = runTest {
        // Given
        val request = GoogleAuthRequest(idToken = "invalid_token")
        val errorResponse: Response<ApiResponse<LoginResponse>> = Response.error(
            401,
            """{"success":false,"message":"Invalid Google token"}""".toResponseBody()
        )

        `when`(apiService.loginWithGoogle(request)).thenReturn(errorResponse)

        // When
        val result = authRemoteDataSource.loginWithGoogle(request)

        // Then
        verify(apiService).loginWithGoogle(request)
        assertEquals(401, result.code())
    }

    // ============================================
    // REGISTER TESTS
    // ============================================

    @Test
    fun `register success - returns successful response`() = runTest {
        // Given
        val request = RegisterRequest(
            username = "newuser",
            email = "newuser@example.com",
            password = "password123",
            fullname = "New User",
            phone = "08123456789"
        )
        val registerResponse = RegisterResponse(
            id = 1,
            username = "newuser",
            email = "newuser@example.com",
            message = "Registration successful"
        )
        val apiResponse = ApiResponse(
            success = true,
            message = "User registered",
            data = registerResponse
        )
        val expectedResponse = Response.success(apiResponse)

        `when`(apiService.register(request)).thenReturn(expectedResponse)

        // When
        val result = authRemoteDataSource.register(request)

        // Then
        verify(apiService).register(request)
        assertTrue(result.isSuccessful)
        assertEquals(apiResponse, result.body())
    }

    @Test
    fun `register failure - returns error response`() = runTest {
        // Given
        val request = RegisterRequest(
            username = "existinguser",
            email = "existing@example.com",
            password = "password123",
            fullname = "Existing User",
            phone = "08123456789"
        )
        val errorResponse: Response<ApiResponse<RegisterResponse>> = Response.error(
            409,
            """{"success":false,"message":"Username already exists"}""".toResponseBody()
        )

        `when`(apiService.register(request)).thenReturn(errorResponse)

        // When
        val result = authRemoteDataSource.register(request)

        // Then
        verify(apiService).register(request)
        assertEquals(409, result.code())
    }

    // ============================================
    // LOGOUT TESTS
    // ============================================

    @Test
    fun `logout success - returns successful response`() = runTest {
        // Given
        val apiResponse = ApiResponse<Unit>(
            success = true,
            message = "Logout successful"
        )
        val expectedResponse = Response.success(apiResponse)

        `when`(apiService.logout()).thenReturn(expectedResponse)

        // When
        val result = authRemoteDataSource.logout()

        // Then
        verify(apiService).logout()
        assertTrue(result.isSuccessful)
        assertEquals(apiResponse, result.body())
    }

    @Test
    fun `logout failure - returns error response`() = runTest {
        // Given
        val errorResponse: Response<ApiResponse<Unit>> = Response.error(
            401,
            """{"success":false,"message":"Unauthorized"}""".toResponseBody()
        )

        `when`(apiService.logout()).thenReturn(errorResponse)

        // When
        val result = authRemoteDataSource.logout()

        // Then
        verify(apiService).logout()
        assertEquals(401, result.code())
    }

    // ============================================
    // FORGOT PASSWORD TESTS
    // ============================================

    @Test
    fun `forgotPassword success - returns successful response`() = runTest {
        // Given
        val request = ForgotPasswordRequest(email = "user@example.com")
        val apiResponse = ApiResponse<Unit>(
            success = true,
            message = "Password reset email sent"
        )
        val expectedResponse = Response.success(apiResponse)

        `when`(apiService.forgotPassword(request)).thenReturn(expectedResponse)

        // When
        val result = authRemoteDataSource.forgotPassword(request)

        // Then
        verify(apiService).forgotPassword(request)
        assertTrue(result.isSuccessful)
        assertEquals(apiResponse, result.body())
    }

    @Test
    fun `forgotPassword failure - returns error response`() = runTest {
        // Given
        val request = ForgotPasswordRequest(email = "nonexistent@example.com")
        val errorResponse: Response<ApiResponse<Unit>> = Response.error(
            404,
            """{"success":false,"message":"User not found"}""".toResponseBody()
        )

        `when`(apiService.forgotPassword(request)).thenReturn(errorResponse)

        // When
        val result = authRemoteDataSource.forgotPassword(request)

        // Then
        verify(apiService).forgotPassword(request)
        assertEquals(404, result.code())
    }

    // ============================================
    // RESET PASSWORD TESTS
    // ============================================

    @Test
    fun `resetPassword success - returns successful response`() = runTest {
        // Given
        val request = ResetPasswordRequest(
            token = "reset_token_123",
            newPassword = "newpassword123",
            confirmPassword = "newpassword123"
        )
        val apiResponse = ApiResponse<Unit>(
            success = true,
            message = "Password reset successful"
        )
        val expectedResponse = Response.success(apiResponse)

        `when`(apiService.resetPassword(request)).thenReturn(expectedResponse)

        // When
        val result = authRemoteDataSource.resetPassword(request)

        // Then
        verify(apiService).resetPassword(request)
        assertTrue(result.isSuccessful)
        assertEquals(apiResponse, result.body())
    }

    @Test
    fun `resetPassword failure - returns error response`() = runTest {
        // Given
        val request = ResetPasswordRequest(
            token = "invalid_token",
            newPassword = "newpassword123",
            confirmPassword = "newpassword123"
        )
        val errorResponse: Response<ApiResponse<Unit>> = Response.error(
            400,
            """{"success":false,"message":"Invalid or expired token"}""".toResponseBody()
        )

        `when`(apiService.resetPassword(request)).thenReturn(errorResponse)

        // When
        val result = authRemoteDataSource.resetPassword(request)

        // Then
        verify(apiService).resetPassword(request)
        assertEquals(400, result.code())
    }
}
