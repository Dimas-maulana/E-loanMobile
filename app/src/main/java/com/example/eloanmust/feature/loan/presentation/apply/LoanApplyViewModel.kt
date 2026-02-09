package com.example.eloanmust.feature.loan.presentation.apply

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.core.common.UiEvent
import com.example.eloanmust.core.common.toRupiah
import com.example.eloanmust.core.datastore.TokenManager
import com.example.eloanmust.core.network.ApiService
import com.example.eloanmust.core.network.safeApiCall
import com.example.eloanmust.feature.loan.domain.model.LoanApplication
import com.example.eloanmust.feature.loan.domain.repository.LoanRepository
import com.example.eloanmust.feature.product.data.dto.PlafondDto
import com.example.eloanmust.feature.product.domain.repository.PlafondRepository
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

data class LoanApplyState(
    val amount: String = "",
    val tenor: String = "",
    val purpose: String = "",
    val selectedPlafond: PlafondDto? = null,
    val plafonds: List<PlafondDto> = emptyList(),
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val isProfileComplete: Boolean = true,
    val isSuccess: Boolean = false,
    val amountError: String? = null,
    val tenorError: String? = null,
    val purposeError: String? = null
)

@HiltViewModel
class LoanApplyViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager,
    private val plafondRepository: PlafondRepository,
    private val loanRepository: LoanRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoanApplyState())
    val state: StateFlow<LoanApplyState> = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadPlafonds()
        checkProfileStatus()
    }

    /**
     * Load plafonds with offline-first strategy.
     * Shows cached plafonds immediately, then updates from API if online.
     */
    private fun loadPlafonds() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            plafondRepository.getPlafonds().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Timber.d("LoanApply: Loaded ${result.data.size} plafonds (offline-first)")
                        _state.update {
                            it.copy(
                                isLoading = false,
                                plafonds = result.data,
                                selectedPlafond = it.selectedPlafond ?: result.data.firstOrNull()
                            )
                        }
                    }
                    is Resource.Error -> {
                        Timber.e("LoanApply: Failed to load plafonds: ${result.message}")
                        // Try cached plafonds as fallback
                        val cached = plafondRepository.getCachedPlafonds()
                        if (cached.isNotEmpty()) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    plafonds = cached,
                                    selectedPlafond = it.selectedPlafond ?: cached.firstOrNull()
                                )
                            }
                        } else {
                            _state.update { it.copy(isLoading = false) }
                            _uiEvent.send(UiEvent.ShowSnackbar(result.message))
                        }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    else -> _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun checkProfileStatus() {
        viewModelScope.launch {
            val result = safeApiCall { apiService.getProfileStatus() }
            if (result is Resource.Success) {
                _state.update { it.copy(isProfileComplete = result.data.isComplete) }
            }
        }
    }

    fun onEvent(event: LoanApplyEvent) {
        when (event) {
            is LoanApplyEvent.AmountChanged -> {
                _state.update { it.copy(amount = event.value, amountError = null) }
                detectPlafond(event.value)
            }
            is LoanApplyEvent.TenorChanged -> {
                _state.update { it.copy(tenor = event.value, tenorError = null) }
            }
            is LoanApplyEvent.PurposeChanged -> {
                _state.update { it.copy(purpose = event.value, purposeError = null) }
            }
            is LoanApplyEvent.PlafondSelected -> {
                _state.update { it.copy(selectedPlafond = event.plafond) }
            }
            is LoanApplyEvent.Submit -> submitApplication(event.latitude, event.longitude)
            is LoanApplyEvent.DismissSuccessDialog -> {
                _state.update { it.copy(isSuccess = false) }
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.Navigate("history"))
                }
            }
        }
    }

    private fun detectPlafond(amountStr: String) {
        val amount = amountStr.replace("[^0-9]".toRegex(), "").toDoubleOrNull() ?: return
        val amountLong = amount.toLong()

        viewModelScope.launch {
            val result = safeApiCall { apiService.detectPlafond(amountLong) }
            if (result is Resource.Success && result.data != null) {
                _state.update { it.copy(selectedPlafond = result.data) }
            }
        }
    }

    private fun submitApplication(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val currentState = _state.value

            // Validation
            val amount = currentState.amount.replace("[^0-9]".toRegex(), "").toDoubleOrNull()
            val tenor = currentState.tenor.toIntOrNull()

            var hasError = false

            if (amount == null || amount <= 0) {
                _state.update { it.copy(amountError = "Masukkan jumlah pinjaman yang valid") }
                hasError = true
            }

            if (tenor == null || tenor <= 0) {
                _state.update { it.copy(tenorError = "Masukkan tenor yang valid") }
                hasError = true
            }

            // Plafond Limit Validation
            currentState.selectedPlafond?.let { plafond ->
                if (amount != null && (amount < plafond.minAmount || amount > plafond.maxAmount)) {
                    _state.update { it.copy(amountError = "Nominal harus antara ${plafond.minAmount.toRupiah()} - ${plafond.maxAmount.toRupiah()}") }
                    hasError = true
                }

                val effectiveMaxTenor = if ((plafond.maxTenor ?: 0) > 0) plafond.maxTenor!! else 60
                if (tenor != null && tenor > effectiveMaxTenor) {
                    _state.update { it.copy(tenorError = "Maksimal tenor adalah $effectiveMaxTenor bulan") }
                    hasError = true
                }
            }

            if (hasError) return@launch

            if (!currentState.isProfileComplete) {
                _uiEvent.send(UiEvent.ShowSnackbar("Lengkapi profil Anda terlebih dahulu"))
                return@launch
            }

            _state.update { it.copy(isSubmitting = true) }

            // Use LoanRepository for offline-first support
            val application = LoanApplication(
                amount = amount!!,
                tenor = tenor!!,
                purpose = currentState.purpose.takeIf { it.isNotBlank() },
                latitude = latitude,
                longitude = longitude
            )

            val result = loanRepository.applyLoan(application)

            when (result) {
                is Resource.Success -> {
                    Timber.d("Loan application submitted via repository: ${result.data}")
                    _state.update { it.copy(isSubmitting = false, isSuccess = true) }
                    // Dialog will handle navigation
                }
                is Resource.Error -> {
                    _state.update { it.copy(isSubmitting = false) }
                    _uiEvent.send(UiEvent.ShowSnackbar(result.message))
                }
                else -> _state.update { it.copy(isSubmitting = false) }
            }
        }
    }
}

sealed class LoanApplyEvent {
    data class AmountChanged(val value: String) : LoanApplyEvent()
    data class TenorChanged(val value: String) : LoanApplyEvent()
    data class PurposeChanged(val value: String) : LoanApplyEvent()
    data class PlafondSelected(val plafond: PlafondDto) : LoanApplyEvent()
    data class Submit(val latitude: Double, val longitude: Double) : LoanApplyEvent()
    data object DismissSuccessDialog : LoanApplyEvent()
}
