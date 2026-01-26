package com.example.eloanmust.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.core.common.UiEvent
import com.example.eloanmust.core.firebase.FcmTokenManager
import com.example.eloanmust.feature.auth.domain.model.LoginCredentials
import com.example.eloanmust.feature.auth.domain.usecase.LoginUseCase
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
 * Login screen state
 */
data class LoginState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val usernameError: String? = null,
    val passwordError: String? = null
)

/**
 * ViewModel for Login screen
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val fcmTokenManager: FcmTokenManager
) : ViewModel() {
    
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()
    
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    
    /**
     * Handle login form events
     */
    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.UsernameChanged -> {
                _state.update { it.copy(username = event.value, usernameError = null) }
            }
            is LoginEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.value, passwordError = null) }
            }
            is LoginEvent.TogglePasswordVisibility -> {
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            is LoginEvent.Login -> {
                login()
            }
            is LoginEvent.NavigateToRegister -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.Navigate("register"))
                }
            }
            is LoginEvent.NavigateToForgotPassword -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.Navigate("forgot_password"))
                }
            }
        }
    }
    
    /**
     * Execute login
     */
    private fun login() {
        viewModelScope.launch {
            val currentState = _state.value
            
            // Basic validation
            if (currentState.username.isBlank()) {
                _state.update { it.copy(usernameError = "Username tidak boleh kosong") }
                return@launch
            }
            
            if (currentState.password.isBlank()) {
                _state.update { it.copy(passwordError = "Password tidak boleh kosong") }
                return@launch
            }
            
            _state.update { it.copy(isLoading = true) }
            
            try {
                // Get FCM token
                val fcmToken = try {
                    fcmTokenManager.getToken()
                } catch (e: Exception) {
                    Timber.e(e, "Failed to get FCM token, using empty string")
                    ""
                }
                
                // Save FCM token
                if (fcmToken.isNotBlank()) {
                    fcmTokenManager.saveToken(fcmToken)
                }
                
                // Create credentials
                val credentials = LoginCredentials(
                    username = currentState.username.trim(),
                    password = currentState.password,
                    fcmToken = fcmToken
                )
                
                // Execute login
                when (val result = loginUseCase(credentials)) {
                    is Resource.Success -> {
                        Timber.d("Login successful: ${result.data.user.username}")
                        _state.update { it.copy(isLoading = false) }
                        _uiEvent.send(UiEvent.ShowSnackbar("Login berhasil!"))
                        _uiEvent.send(UiEvent.Navigate("home"))
                    }
                    is Resource.Error -> {
                        Timber.e("Login failed: ${result.message}")
                        _state.update { it.copy(isLoading = false) }
                        _uiEvent.send(UiEvent.ShowSnackbar(result.message))
                    }
                    is Resource.Loading -> {
                        // Already in loading state
                    }
                    is Resource.Idle -> {
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Login error")
                _state.update { it.copy(isLoading = false) }
                _uiEvent.send(UiEvent.ShowSnackbar("Terjadi kesalahan: ${e.message}"))
            }
        }
    }
}

/**
 * Login screen events
 */
sealed class LoginEvent {
    data class UsernameChanged(val value: String) : LoginEvent()
    data class PasswordChanged(val value: String) : LoginEvent()
    data object TogglePasswordVisibility : LoginEvent()
    data object Login : LoginEvent()
    data object NavigateToRegister : LoginEvent()
    data object NavigateToForgotPassword : LoginEvent()
}
