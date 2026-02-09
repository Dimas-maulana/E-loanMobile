package com.example.eloanmust.core.firebase

import com.example.eloanmust.core.datastore.TokenManager
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * FCM Token Manager for handling Firebase Cloud Messaging tokens.
 * Manages token generation, storage, and retrieval.
 */
@Singleton
class FcmTokenManager @Inject constructor(
    private val tokenManager: TokenManager
) {
    private val _currentToken = MutableStateFlow<String?>(null)

    /**
     * Current FCM token as Flow
     */
    val currentToken: Flow<String?> = _currentToken.asStateFlow()

    /**
     * Get FCM token, generating new one if needed.
     * This is a suspend function that blocks until token is available.
     */
    suspend fun getToken(): String {
        // First, check if we have a cached token
        val cachedToken = tokenManager.fcmToken.first()
        if (!cachedToken.isNullOrBlank()) {
            _currentToken.value = cachedToken
            return cachedToken
        }

        // Generate new token
        return generateNewToken()
    }

    /**
     * Get token or null if not available immediately
     */
    fun getTokenOrNull(): String? = _currentToken.value

    /**
     * Generate new FCM token from Firebase
     */
    suspend fun generateNewToken(): String {
        return suspendCancellableCoroutine { continuation ->
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    Timber.d("FCM Token generated: $token")
                    _currentToken.value = token
                    continuation.resume(token)
                }
                .addOnFailureListener { exception ->
                    Timber.e(exception, "Failed to get FCM token")
                    continuation.resumeWithException(exception)
                }
        }
    }

    /**
     * Save FCM token to persistent storage
     */
    suspend fun saveToken(token: String) {
        _currentToken.value = token
        tokenManager.saveFcmToken(token)
        Timber.d("FCM Token saved to storage")
    }

    /**
     * Update token when Firebase generates a new one
     */
    suspend fun updateToken(newToken: String) {
        val oldToken = _currentToken.value
        if (oldToken != newToken) {
            Timber.d("FCM Token updated from $oldToken to $newToken")
            saveToken(newToken)
        }
    }

    /**
     * Clear cached token (for testing)
     */
    fun clearCachedToken() {
        _currentToken.value = null
    }

    /**
     * Subscribe to a topic
     */
    fun subscribeToTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnSuccessListener {
                Timber.d("Subscribed to topic: $topic")
            }
            .addOnFailureListener { exception ->
                Timber.e(exception, "Failed to subscribe to topic: $topic")
            }
    }

    /**
     * Unsubscribe from a topic
     */
    fun unsubscribeFromTopic(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
            .addOnSuccessListener {
                Timber.d("Unsubscribed from topic: $topic")
            }
            .addOnFailureListener { exception ->
                Timber.e(exception, "Failed to unsubscribe from topic: $topic")
            }
    }
}
