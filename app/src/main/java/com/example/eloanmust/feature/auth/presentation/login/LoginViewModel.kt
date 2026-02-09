package com.example.eloanmust.feature.auth.presentation.login

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eloanmust.core.auth.GoogleAuthHelper
import com.example.eloanmust.core.auth.GoogleSignInResult
import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.core.common.UiEvent
import com.example.eloanmust.core.firebase.FcmTokenManager
import com.example.eloanmust.feature.auth.domain.model.LoginCredentials
import com.example.eloanmust.feature.auth.domain.usecase.GoogleSignInUseCase
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
    val isGoogleLoading: Boolean = false,
    val isSuccess: Boolean = false,
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
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val googleAuthHelper: GoogleAuthHelper,
    private val fcmTokenManager: FcmTokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    // Channel for Google Sign-In intent
    private val _googleSignInIntent = Channel<Intent>()
    val googleSignInIntent = _googleSignInIntent.receiveAsFlow()

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
            is LoginEvent.GoogleSignIn -> {
                startGoogleSignIn()
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
            is LoginEvent.DismissSuccessDialog -> {
                _state.update { it.copy(isSuccess = false) }
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.Navigate("home"))
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
                        _state.update { it.copy(isLoading = false, isSuccess = true) }
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

    /**
     * Start Google Sign-In flow
     */
    private fun startGoogleSignIn() {
        viewModelScope.launch {
            _state.update { it.copy(isGoogleLoading = true) }
            try {
                val intent = googleAuthHelper.getSignInIntent()
                _googleSignInIntent.send(intent)
            } catch (e: Exception) {
                Timber.e(e, "Failed to start Google Sign-In")
                _state.update { it.copy(isGoogleLoading = false) }
                _uiEvent.send(UiEvent.ShowSnackbar("Gagal memulai login Google: ${e.message}"))
            }
        }
    }

    /**
     * Handle Google Sign-In result from Activity
     */
    fun onGoogleSignInResult(data: Intent?) {
        viewModelScope.launch {
            _state.update { it.copy(isGoogleLoading = true) }

            try {
                when (val result = googleAuthHelper.handleSignInResult(data)) {
                    is GoogleSignInResult.Success -> {
                        Timber.d("Got Firebase ID Token, sending to backend...")

                        // Get FCM token
                        val fcmToken = try {
                            fcmTokenManager.getToken()
                        } catch (e: Exception) {
                            Timber.e(e, "Failed to get FCM token")
                            null
                        }

                        // Send to backend
                        when (val loginResult = googleSignInUseCase(result.idToken, fcmToken)) {
                            is Resource.Success -> {
                                Timber.d("Google login successful: ${loginResult.data.user.username}")
                                _state.update { it.copy(isGoogleLoading = false, isSuccess = true) }
                            }
                            is Resource.Error -> {
                                Timber.e("Google login failed: ${loginResult.message}")
                                _state.update { it.copy(isGoogleLoading = false) }
                                _uiEvent.send(UiEvent.ShowSnackbar(loginResult.message))
                            }
                            is Resource.Loading -> { /* Loading state handled */ }
                            is Resource.Idle -> {
                                _state.update { it.copy(isGoogleLoading = false) }
                            }
                        }
                    }
                    is GoogleSignInResult.Error -> {
                        Timber.e("Google Sign-In error: ${result.message}")
                        _state.update { it.copy(isGoogleLoading = false) }
                        _uiEvent.send(UiEvent.ShowSnackbar(result.message))
                    }
                    is GoogleSignInResult.Cancelled -> {
                        Timber.d("Google Sign-In cancelled by user")
                        _state.update { it.copy(isGoogleLoading = false) }
                        // No snackbar for cancelled - user intentionally cancelled
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error handling Google Sign-In result")
                _state.update { it.copy(isGoogleLoading = false) }
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
    data object GoogleSignIn : LoginEvent()
    data object NavigateToRegister : LoginEvent()
    data object NavigateToForgotPassword : LoginEvent()
    data object DismissSuccessDialog : LoginEvent()
}
