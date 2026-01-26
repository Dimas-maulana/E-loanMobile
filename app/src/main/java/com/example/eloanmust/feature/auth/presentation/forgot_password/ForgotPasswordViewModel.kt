package com.example.eloanmust.feature.auth.presentation.forgot_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.core.common.UiEvent
import com.example.eloanmust.feature.auth.domain.usecase.ForgotPasswordUseCase
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

data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(ForgotPasswordState())
    val state: StateFlow<ForgotPasswordState> = _state.asStateFlow()
    
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    
    fun onEvent(event: ForgotPasswordEvent) {
        when (event) {
            is ForgotPasswordEvent.EmailChanged -> {
                _state.update { it.copy(email = event.value, emailError = null) }
            }
            is ForgotPasswordEvent.Submit -> {
                submitForgotPassword()
            }
            is ForgotPasswordEvent.NavigateToLogin -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.Navigate("login"))
                }
            }
        }
    }
    
    private fun submitForgotPassword() {
        viewModelScope.launch {
            val email = _state.value.email
            
            if (email.isBlank()) {
                _state.update { it.copy(emailError = "Email tidak boleh kosong") }
                return@launch
            }
            
            _state.update { it.copy(isLoading = true) }
            
            when (val result = forgotPasswordUseCase(email)) {
                is Resource.Success -> {
                    Timber.d("Forgot password request successful")
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                    _uiEvent.send(UiEvent.ShowSnackbar("Link reset password telah dikirim ke email Anda"))
                }
                is Resource.Error -> {
                    Timber.e("Forgot password failed: ${result.message}")
                    _state.update { it.copy(isLoading = false) }
                    _uiEvent.send(UiEvent.ShowSnackbar(result.message))
                }
                else -> {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }
}

sealed class ForgotPasswordEvent {
    data class EmailChanged(val value: String) : ForgotPasswordEvent()
    data object Submit : ForgotPasswordEvent()
    data object NavigateToLogin : ForgotPasswordEvent()
}
