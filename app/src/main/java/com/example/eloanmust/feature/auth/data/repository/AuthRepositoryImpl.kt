package com.example.eloanmust.feature.auth.data.repository

import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.core.network.safeApiCall
import com.example.eloanmust.feature.auth.data.datasource.AuthLocalDataSource
import com.example.eloanmust.feature.auth.data.datasource.AuthRemoteDataSource
import com.example.eloanmust.feature.auth.data.dto.ForgotPasswordRequest
import com.example.eloanmust.feature.auth.data.dto.ResetPasswordRequest
import com.example.eloanmust.feature.auth.data.mapper.toDomain
import com.example.eloanmust.feature.auth.data.mapper.toRequest
import com.example.eloanmust.feature.auth.data.mapper.toUserSession
import com.example.eloanmust.feature.auth.domain.model.LoginCredentials
import com.example.eloanmust.feature.auth.domain.model.RegistrationData
import com.example.eloanmust.feature.auth.domain.model.User
import com.example.eloanmust.feature.auth.domain.model.UserSession
import com.example.eloanmust.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository.
 * Handles auth operations with remote and local data sources.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: AuthLocalDataSource
) : AuthRepository {
    
    override suspend fun login(credentials: LoginCredentials): Resource<UserSession> {
        Timber.d("Attempting login for user: ${credentials.username}")
        
        val result = safeApiCall {
            remoteDataSource.login(credentials.toRequest())
        }
        
        return when (result) {
            is Resource.Success -> {
                val userSession = result.data.toUserSession()
                
                // Save session to local storage
                localDataSource.saveLoginSession(
                    accessToken = userSession.accessToken,
                    refreshToken = userSession.refreshToken,
                    userId = userSession.user.id,
                    username = userSession.user.username,
                    email = userSession.user.email,
                    role = userSession.user.role
                )
                
                Timber.d("Login successful for user: ${userSession.user.username}")
                Resource.Success(userSession)
            }
            is Resource.Error -> {
                Timber.e("Login failed: ${result.message}")
                Resource.Error(result.message, result.code, result.exception)
            }
            is Resource.Loading -> Resource.Loading
            is Resource.Idle -> Resource.Idle
        }
    }
    
    override suspend fun register(data: RegistrationData): Resource<User> {
        Timber.d("Attempting registration for user: ${data.username}")
        
        val result = safeApiCall {
            remoteDataSource.register(data.toRequest())
        }
        
        return when (result) {
            is Resource.Success -> {
                Timber.d("Registration successful for user: ${data.username}")
                Resource.Success(result.data.toDomain())
            }
            is Resource.Error -> {
                Timber.e("Registration failed: ${result.message}")
                Resource.Error(result.message, result.code, result.exception)
            }
            is Resource.Loading -> Resource.Loading
            is Resource.Idle -> Resource.Idle
        }
    }
    
    override suspend fun logout(): Resource<Unit> {
        Timber.d("Attempting logout")
        
        // First, try to logout from server
        val result = safeApiCall {
            remoteDataSource.logout()
        }
        
        // Always clear local session, even if server logout fails
        localDataSource.clearLoginSession()
        
        return when (result) {
            is Resource.Success -> {
                Timber.d("Logout successful")
                Resource.Success(Unit)
            }
            is Resource.Error -> {
                // Still return success since local session is cleared
                Timber.w("Server logout failed, but local session cleared: ${result.message}")
                Resource.Success(Unit)
            }
            is Resource.Loading -> Resource.Loading
            is Resource.Idle -> {
                // Local logout already done
                Resource.Success(Unit)
            }
        }
    }
    
    override suspend fun forgotPassword(email: String): Resource<Unit> {
        Timber.d("Requesting password reset for: $email")
        
        val result = safeApiCall {
            remoteDataSource.forgotPassword(ForgotPasswordRequest(email))
        }
        
        return when (result) {
            is Resource.Success -> {
                Timber.d("Password reset request sent")
                Resource.Success(Unit)
            }
            is Resource.Error -> {
                Timber.e("Password reset request failed: ${result.message}")
                Resource.Error(result.message, result.code, result.exception)
            }
            is Resource.Loading -> Resource.Loading
            is Resource.Idle -> Resource.Idle
        }
    }
    
    override suspend fun resetPassword(token: String, newPassword: String): Resource<Unit> {
        Timber.d("Attempting password reset")
        
        val result = safeApiCall {
            remoteDataSource.resetPassword(
                ResetPasswordRequest(
                    token = token,
                    newPassword = newPassword,
                    confirmPassword = newPassword
                )
            )
        }
        
        return when (result) {
            is Resource.Success -> {
                Timber.d("Password reset successful")
                Resource.Success(Unit)
            }
            is Resource.Error -> {
                Timber.e("Password reset failed: ${result.message}")
                Resource.Error(result.message, result.code, result.exception)
            }
            is Resource.Loading -> Resource.Loading
            is Resource.Idle -> Resource.Idle
        }
    }
    
    override fun isLoggedIn(): Flow<Boolean> {
        return localDataSource.isLoggedIn()
    }
    
    override fun getCurrentUserId(): Flow<Long?> {
        return localDataSource.getCurrentUserId()
    }
    
    override fun getAccessToken(): Flow<String?> {
        return localDataSource.getAccessToken()
    }
}
