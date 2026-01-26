package com.example.eloanmust.core.network

import com.example.eloanmust.core.datastore.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor that adds JWT Bearer token to authenticated requests.
 * Reads token from TokenManager and attaches it to the Authorization header.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    
    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
        
        // Endpoints that don't require authentication
        private val PUBLIC_ENDPOINTS = listOf(
            "api/auth/login",
            "api/auth/register",
            "api/auth/forgot-password",
            "api/auth/reset-password",
            "api/plafonds",
            "api/loans/simulate"
        )
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestUrl = originalRequest.url.toString()
        
        // Check if this is a public endpoint
        val isPublicEndpoint = PUBLIC_ENDPOINTS.any { requestUrl.contains(it) }
        
        // If public endpoint, proceed without token
        if (isPublicEndpoint) {
            return chain.proceed(originalRequest)
        }
        
        // Get token from TokenManager (blocking call for interceptor)
        val token = runBlocking {
            tokenManager.accessToken.first()
        }
        
        // If no token available, proceed without it
        if (token.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }
        
        // Add Authorization header with Bearer token
        val authenticatedRequest = originalRequest.newBuilder()
            .header(AUTHORIZATION_HEADER, "$BEARER_PREFIX$token")
            .build()
        
        return chain.proceed(authenticatedRequest)
    }
}
