package com.example.eloanmust.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eloanmust.core.common.Constants
import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.core.common.UiEvent
import com.example.eloanmust.core.datastore.TokenManager
import com.example.eloanmust.core.network.ApiService
import com.example.eloanmust.core.network.safeApiCall
import com.example.eloanmust.feature.profile.data.dto.CustomerProfileRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject

data class ProfileState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isEditing: Boolean = false,
    val isLoggedIn: Boolean = false,
    val username: String = "",
    val email: String = "",
    val fullName: String = "",
    val nik: String = "",
    val birthDate: String = "",
    val address: String = "",
    val bankName: String = "",
    val bankAccountNumber: String = "",
    val bankAccountName: String = "",
    val ktpImageUrl: String? = null,
    val isProfileComplete: Boolean = false,
    val missingFields: List<String> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        checkLoginAndLoadProfile()
    }

    private fun checkLoginAndLoadProfile() {
        viewModelScope.launch {
            val isLoggedIn = tokenManager.isLoggedIn.first()
            _state.update { it.copy(isLoggedIn = isLoggedIn) }

            if (isLoggedIn) {
                loadProfile()
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val username = tokenManager.username.first() ?: ""
            val email = tokenManager.userEmail.first() ?: ""

            val result = safeApiCall { apiService.getProfile() }

            when (result) {
                is Resource.Success -> {
                    val profile = result.data
                    _state.update {
                        it.copy(
                            isLoading = false,
                            username = username,
                            email = email,
                            fullName = profile.fullName ?: "",
                            nik = profile.nik ?: "",
                            birthDate = profile.birthDate ?: "",

                            address = profile.address ?: "",
                            bankName = profile.bankName ?: "",
                            bankAccountNumber = profile.bankAccountNumber ?: "",
                            bankAccountName = profile.bankAccountName ?: "",
                            ktpImageUrl = "${Constants.BASE_URL}${profile.ktpImageUrl}",
                            error = null
                        )
                    }
                    checkProfileStatus()
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, username = username, email = email) }
                    // Profile might not exist yet, that's okay
                    Timber.d("Profile not found or error: ${result.message}")
                }
                else -> _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun checkProfileStatus() {
        viewModelScope.launch {
            val result = safeApiCall { apiService.getProfileStatus() }
            if (result is Resource.Success) {
                val status = result.data
                val isApiComplete = status.isComplete

                // Fallback: Check local data if API says incomplete
                val isLocalComplete = checkLocalProfileCompletion()
                val finalIsComplete = isApiComplete || isLocalComplete

                Timber.d("Profile check - API: $isApiComplete, Local: $isLocalComplete -> Final: $finalIsComplete")

                _state.update {
                    it.copy(
                        isProfileComplete = finalIsComplete,
                        missingFields = status.missingFields ?: emptyList()
                    )
                }
            } else if (result is Resource.Error) {
                // If API fails, fallback to local check
                val isLocalComplete = checkLocalProfileCompletion()
                Timber.d("Profile API failed, falling back to local check: $isLocalComplete")
                _state.update { it.copy(isProfileComplete = isLocalComplete) }
            }
        }
    }

    private fun checkLocalProfileCompletion(): Boolean {
        val s = _state.value
        return s.fullName.isNotBlank() &&
            s.nik.isNotBlank() &&
            s.birthDate.isNotBlank() &&
            s.address.isNotBlank() &&
            s.bankName.isNotBlank() &&
            s.bankAccountNumber.isNotBlank() &&
            s.bankAccountName.isNotBlank() &&
            !s.ktpImageUrl.isNullOrBlank()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.ToggleEdit -> _state.update { it.copy(isEditing = !it.isEditing) }
            is ProfileEvent.FullNameChanged -> _state.update { it.copy(fullName = event.value) }
            is ProfileEvent.NikChanged -> _state.update { it.copy(nik = event.value) }
            is ProfileEvent.BirthDateChanged -> _state.update { it.copy(birthDate = event.value) }

            is ProfileEvent.AddressChanged -> _state.update { it.copy(address = event.value) }
            is ProfileEvent.BankNameChanged -> _state.update { it.copy(bankName = event.value) }
            is ProfileEvent.BankAccountNumberChanged -> _state.update { it.copy(bankAccountNumber = event.value) }
            is ProfileEvent.BankAccountNameChanged -> _state.update { it.copy(bankAccountName = event.value) }
            is ProfileEvent.Save -> saveProfile()
            is ProfileEvent.Logout -> logout()
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            val currentState = _state.value

            _state.update { it.copy(isSaving = true) }

            val request = CustomerProfileRequest(
                fullName = currentState.fullName,
                nik = currentState.nik,
                birthDate = currentState.birthDate,
                address = currentState.address,

                bankName = currentState.bankName.ifBlank { null },
                bankAccountNumber = currentState.bankAccountNumber.ifBlank { null },
                bankAccountName = currentState.bankAccountName.ifBlank { null }
            )

            val result = safeApiCall { apiService.updateProfile(request) }

            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(isSaving = false, isEditing = false) }
                    _uiEvent.send(UiEvent.ShowSnackbar("Profil berhasil disimpan"))
                    checkProfileStatus()
                }
                is Resource.Error -> {
                    _state.update { it.copy(isSaving = false) }
                    _uiEvent.send(UiEvent.ShowSnackbar(result.message))
                }
                else -> _state.update { it.copy(isSaving = false) }
            }
        }
    }

    fun uploadKtp(file: File) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val result = safeApiCall { apiService.uploadKtp(body) }

            when (result) {
                is Resource.Success -> {
                    val ktpUrl = result.data
                    val formattedUrl = if (ktpUrl?.startsWith("/") == true) {
                        "${Constants.BASE_URL}$ktpUrl"
                    } else {
                        ktpUrl
                    }
                    _state.update {
                        it.copy(
                            isSaving = false,
                            ktpImageUrl = formattedUrl
                        )
                    }
                    _uiEvent.send(UiEvent.ShowSnackbar("KTP berhasil diupload"))
                    checkProfileStatus()
                }
                is Resource.Error -> {
                    _state.update { it.copy(isSaving = false) }
                    _uiEvent.send(UiEvent.ShowSnackbar("Gagal upload KTP: ${result.message}"))
                }
                else -> _state.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            tokenManager.clearLoginData()
            _uiEvent.send(UiEvent.Navigate("login"))
        }
    }
}

sealed class ProfileEvent {
    data object ToggleEdit : ProfileEvent()
    data class FullNameChanged(val value: String) : ProfileEvent()
    data class NikChanged(val value: String) : ProfileEvent()
    data class BirthDateChanged(val value: String) : ProfileEvent()

    data class AddressChanged(val value: String) : ProfileEvent()
    data class BankNameChanged(val value: String) : ProfileEvent()
    data class BankAccountNumberChanged(val value: String) : ProfileEvent()
    data class BankAccountNameChanged(val value: String) : ProfileEvent()
    data object Save : ProfileEvent()
    data object Logout : ProfileEvent()
}
