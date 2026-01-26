package com.example.eloanmust.feature.auth.domain.usecase

import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.core.common.isValidEmail
import com.example.eloanmust.feature.auth.domain.model.RegistrationData
import com.example.eloanmust.feature.auth.domain.model.User
import com.example.eloanmust.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for user registration.
 */
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Execute registration
     * @param data Registration data
     * @return Resource containing User on success
     */
    suspend operator fun invoke(data: RegistrationData): Resource<User> {
        // Validate inputs
        if (data.username.isBlank()) {
            return Resource.Error("Username tidak boleh kosong")
        }
        
        if (data.username.length < 3) {
            return Resource.Error("Username minimal 3 karakter")
        }
        
        if (data.email.isBlank()) {
            return Resource.Error("Email tidak boleh kosong")
        }
        
        if (!data.email.isValidEmail()) {
            return Resource.Error("Format email tidak valid")
        }
        
        if (data.password.isBlank()) {
            return Resource.Error("Password tidak boleh kosong")
        }
        
        if (data.password.length < 8) {
            return Resource.Error("Password minimal 8 karakter")
        }
        
        if (data.password != data.confirmPassword) {
            return Resource.Error("Konfirmasi password tidak cocok")
        }
        
        return authRepository.register(data)
    }
}
