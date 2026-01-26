package com.example.eloanmust.feature.auth.domain.usecase

import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.core.common.isValidEmail
import com.example.eloanmust.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for forgot password request.
 */
class ForgotPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Execute forgot password request
     * @param email User's email
     * @return Resource<Unit> on success
     */
    suspend operator fun invoke(email: String): Resource<Unit> {
        if (email.isBlank()) {
            return Resource.Error("Email tidak boleh kosong")
        }
        
        if (!email.isValidEmail()) {
            return Resource.Error("Format email tidak valid")
        }
        
        return authRepository.forgotPassword(email)
    }
}
