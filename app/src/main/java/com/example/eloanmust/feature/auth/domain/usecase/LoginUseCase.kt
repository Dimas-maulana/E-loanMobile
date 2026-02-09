package com.example.eloanmust.feature.auth.domain.usecase

import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.feature.auth.domain.model.LoginCredentials
import com.example.eloanmust.feature.auth.domain.model.UserSession
import com.example.eloanmust.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for user login.
 * Clean Architecture: Single responsibility - handles login logic only.
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Execute login with credentials
     * @param credentials User credentials including FCM token
     * @return Resource containing UserSession on success
     */
    suspend operator fun invoke(credentials: LoginCredentials): Resource<UserSession> {
        // Validate inputs before calling repository
        if (credentials.username.isBlank()) {
            return Resource.Error("Username tidak boleh kosong")
        }

        if (credentials.password.isBlank()) {
            return Resource.Error("Password tidak boleh kosong")
        }

        if (credentials.password.length < 6) {
            return Resource.Error("Password minimal 6 karakter")
        }

        return authRepository.login(credentials)
    }
}
