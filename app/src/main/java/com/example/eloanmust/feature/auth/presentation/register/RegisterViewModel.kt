package com.example.eloanmust.feature.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.core.common.UiEvent
import com.example.eloanmust.feature.auth.domain.model.RegistrationData
import com.example.eloanmust.feature.auth.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Register screen state
 */
data class RegisterState(
    val username: String = "",
    val email: String = "",
    val fullname: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val usernameError: String? = null,
    val emailError: String? = null,
    val fullnameError: String? = null,
    val phoneError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

/**
 * ViewModel for Register screen
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.UsernameChanged -> {
                _state.update { it.copy(username = event.value, usernameError = null) }
            }
            is RegisterEvent.EmailChanged -> {
                _state.update { it.copy(email = event.value, emailError = null) }
            }
            is RegisterEvent.FullnameChanged -> {
                _state.update { it.copy(fullname = event.value, fullnameError = null) }
            }
            is RegisterEvent.PhoneChanged -> {
                _state.update { it.copy(phone = event.value, phoneError = null) }
            }
            is RegisterEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.value, passwordError = null) }
            }
            is RegisterEvent.ConfirmPasswordChanged -> {
                _state.update { it.copy(confirmPassword = event.value, confirmPasswordError = null) }
            }
            is RegisterEvent.TogglePasswordVisibility -> {
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            is RegisterEvent.ToggleConfirmPasswordVisibility -> {
                _state.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
            }
            is RegisterEvent.Register -> {
                register()
            }
            is RegisterEvent.NavigateToLogin -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.Navigate("login"))
                }
            }
            is RegisterEvent.DismissSuccessDialog -> {
                _state.update { it.copy(isSuccess = false) }
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.Navigate("login"))
                }
            }
        }
    }

    private fun register() {
        viewModelScope.launch {
            val currentState = _state.value

            // Basic validation
            var hasError = false

            if (currentState.username.isBlank()) {
                _state.update { it.copy(usernameError = "Username tidak boleh kosong") }
                hasError = true
            }

            if (currentState.email.isBlank()) {
                _state.update { it.copy(emailError = "Email tidak boleh kosong") }
                hasError = true
            }

            if (currentState.fullname.isBlank()) {
                _state.update { it.copy(fullnameError = "Nama lengkap tidak boleh kosong") }
                hasError = true
            }

            if (currentState.phone.isBlank()) {
                _state.update { it.copy(phoneError = "Nomor telepon tidak boleh kosong") }
                hasError = true
            }

            if (currentState.password.isBlank()) {
                _state.update { it.copy(passwordError = "Password tidak boleh kosong") }
                hasError = true
            }

            if (currentState.confirmPassword.isBlank()) {
                _state.update { it.copy(confirmPasswordError = "Konfirmasi password tidak boleh kosong") }
                hasError = true
            }

            if (currentState.password != currentState.confirmPassword) {
                _state.update { it.copy(confirmPasswordError = "Password tidak cocok") }
                hasError = true
            }

            if (hasError) return@launch

            _state.update { it.copy(isLoading = true) }

            try {
                val registrationData = RegistrationData(
                    username = currentState.username.trim(),
                    email = currentState.email.trim(),
                    password = currentState.password,
                    confirmPassword = currentState.confirmPassword,
                    fullname = currentState.fullname.trim(),
                    phone = currentState.phone.trim()
                )

                when (val result = registerUseCase(registrationData)) {
                    is Resource.Success -> {
                        Timber.d("Registration successful")
                        _state.update { it.copy(isLoading = false, isSuccess = true) }
                    }
                    is Resource.Error -> {
                        Timber.e("Registration failed: ${result.message}")
                        _state.update { it.copy(isLoading = false) }
                        _uiEvent.send(UiEvent.ShowSnackbar(result.message))
                    }
                    else -> {
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Registration error")
                _state.update { it.copy(isLoading = false) }
                _uiEvent.send(UiEvent.ShowSnackbar("Terjadi kesalahan: ${e.message}"))
            }
        }
    }
}

sealed class RegisterEvent {
    data class UsernameChanged(val value: String) : RegisterEvent()
    data class EmailChanged(val value: String) : RegisterEvent()
    data class FullnameChanged(val value: String) : RegisterEvent()
    data class PhoneChanged(val value: String) : RegisterEvent()
    data class PasswordChanged(val value: String) : RegisterEvent()
    data class ConfirmPasswordChanged(val value: String) : RegisterEvent()
    data object TogglePasswordVisibility : RegisterEvent()
    data object ToggleConfirmPasswordVisibility : RegisterEvent()
    data object Register : RegisterEvent()
    data object NavigateToLogin : RegisterEvent()
    data object DismissSuccessDialog : RegisterEvent()
}
