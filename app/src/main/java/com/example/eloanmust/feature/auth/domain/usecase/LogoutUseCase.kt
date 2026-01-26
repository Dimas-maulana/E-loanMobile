package com.example.eloanmust.feature.auth.domain.usecase

import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for user logout.
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Execute logout
     * @return Resource<Unit> on success
     */
    suspend operator fun invoke(): Resource<Unit> {
        return authRepository.logout()
    }
}
