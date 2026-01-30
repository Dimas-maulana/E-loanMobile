package com.example.eloanmust.feature.auth.domain.usecase

import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.feature.auth.domain.model.UserSession
import com.example.eloanmust.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for Google Sign-In authentication.
 * Sends Firebase ID Token to backend and returns UserSession.
 */
class GoogleSignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Execute Google Sign-In with Firebase ID Token
     * @param idToken Firebase ID Token obtained from Google Sign-In
     * @param fcmToken Optional FCM token for push notifications
     * @return Resource containing UserSession on success
     */
    suspend operator fun invoke(idToken: String, fcmToken: String?): Resource<UserSession> {
        return authRepository.loginWithGoogle(idToken, fcmToken)
    }
}
