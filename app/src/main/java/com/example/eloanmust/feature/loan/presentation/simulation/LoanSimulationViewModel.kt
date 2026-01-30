package com.example.eloanmust.feature.loan.presentation.simulation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.core.common.UiEvent
import com.example.eloanmust.core.datastore.TokenManager
import com.example.eloanmust.core.network.ApiService
import com.example.eloanmust.core.network.safeApiCall
import com.example.eloanmust.feature.loan.data.dto.LoanApplicationRequest
import com.example.eloanmust.feature.loan.data.dto.LoanDto
import com.example.eloanmust.feature.loan.data.dto.LoanSimulationRequest
import com.example.eloanmust.feature.loan.data.dto.LoanSimulationResponse
import com.example.eloanmust.feature.product.data.dto.PlafondDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class LoanSimulationState(
    val amount: String = "5000000",
    val tenor: String = "6",
    val purpose: String = "",
    val selectedPlafond: PlafondDto? = null,
    val simulationResult: LoanSimulationResponse? = null,
    val isLoading: Boolean = false,
    val isSimulating: Boolean = false,
    val isSubmitting: Boolean = false,
    val isProfileComplete: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isSuccess: Boolean = false,
    val showConfirmDialog: Boolean = false,
    val loanResult: LoanDto? = null,
    val simulationError: String? = null,
    val amountError: String? = null,
    val tenorError: String? = null,
    val purposeError: String? = null
)

@HiltViewModel
class LoanSimulationViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {
    
    private val _state = MutableStateFlow(LoanSimulationState())
    val state: StateFlow<LoanSimulationState> = _state.asStateFlow()
    
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    
    // Cache for local detection
    private var allPlafonds: List<PlafondDto> = emptyList()
    
    // Job for debouncing simulation
    private var simulationJob: Job? = null
    
    init {
        checkLoginAndProfileStatus()
        fetchAllPlafonds()
    }
    
    private fun fetchAllPlafonds() {
        viewModelScope.launch {
            val result = safeApiCall { apiService.getPlafonds() }
            if (result is Resource.Success && result.data != null) {
                allPlafonds = result.data
                Timber.d("LoanSim: Loaded ${allPlafonds.size} plafonds")
                allPlafonds.forEach { Timber.d("LoanSim: Product ${it.name} Range: ${it.minAmount}-${it.maxAmount} MaxTenor: ${it.maxTenor}") }
                
                // Trigger initial detection/simulation after loading products
                if (allPlafonds.isNotEmpty()) {
                    val amountLong = _state.value.amount.replace("[^0-9]".toRegex(), "").toLongOrNull() ?: 5000000L
                    detectLocalPlafond(amountLong)
                    calculateLocalSimulation()
                    triggerSimulation()
                }
            } else {
                 Timber.e("LoanSim: Failed to load plafonds")
                 // Fallback: trigger simulation to rely on API
                 triggerSimulation()
            }
        }
    }
    
    private fun checkLoginAndProfileStatus() {
        viewModelScope.launch {
            val isLoggedIn = tokenManager.isLoggedIn.first()
            _state.update { it.copy(isLoggedIn = isLoggedIn) }
            
            if (isLoggedIn) {
                checkProfileStatus()
            }
        }
    }
    
    private fun checkProfileStatus() {
        viewModelScope.launch {
            val result = safeApiCall { apiService.getProfileStatus() }
            when (result) {
                is Resource.Success -> {
                    Timber.d("LoanSim: Profile status check - isComplete: ${result.data.isComplete}")
                    _state.update { it.copy(isProfileComplete = result.data.isComplete) }
                }
                is Resource.Error -> {
                    Timber.e("LoanSim: Profile status check failed: ${result.message}")
                    // If profile status check fails, assume profile is complete to allow loan application
                    // The API will reject if profile is actually incomplete
                    _state.update { it.copy(isProfileComplete = true) }
                }
                else -> {
                    Timber.w("LoanSim: Profile status check returned unexpected result")
                    _state.update { it.copy(isProfileComplete = true) }
                }
            }
        }
    }
    
    fun onEvent(event: LoanSimulationEvent) {
        when (event) {
            is LoanSimulationEvent.AmountChanged -> {
                _state.update { it.copy(amount = event.value, amountError = null) }
                
                val amountLong = event.value.replace("[^0-9]".toRegex(), "").toLongOrNull() ?: 0L
                Timber.d("LoanSim: ===== AmountChanged Event =====")
                Timber.d("LoanSim: Raw amount: ${event.value}, parsed: $amountLong")
                
                detectLocalPlafond(amountLong)
                
                val afterDetection = _state.value
                Timber.d("LoanSim: After detectLocalPlafond -> Plafond: ${afterDetection.selectedPlafond?.name}, MaxTenor: ${afterDetection.selectedPlafond?.maxTenor}, CurrentTenor: ${afterDetection.tenor}")
                
                calculateLocalSimulation()
                
                val afterCalc = _state.value
                Timber.d("LoanSim: After calculateLocalSimulation -> Monthly: ${afterCalc.simulationResult?.monthlyInstallment}")
                Timber.d("LoanSim: ===== End AmountChanged =====")
                
                triggerSimulation()
            }
            is LoanSimulationEvent.TenorChanged -> {
                Timber.d("LoanSim: ===== TenorChanged Event =====")
                Timber.d("LoanSim: New tenor: ${event.value}")
                _state.update { it.copy(tenor = event.value, tenorError = null) }
                calculateLocalSimulation()
                triggerSimulation()
                Timber.d("LoanSim: ===== End TenorChanged =====")
            }
            is LoanSimulationEvent.PurposeChanged -> {
                _state.update { it.copy(purpose = event.value, purposeError = null) }
            }
            is LoanSimulationEvent.Simulate -> simulate()
            is LoanSimulationEvent.ShowConfirmDialog -> showConfirmDialog()
            is LoanSimulationEvent.DismissConfirmDialog -> _state.update { it.copy(showConfirmDialog = false) }
            is LoanSimulationEvent.ConfirmApplyLoan -> {
                _state.update { it.copy(showConfirmDialog = false) }
                applyLoan()
            }
            is LoanSimulationEvent.ApplyLoan -> showConfirmDialog()
            is LoanSimulationEvent.DismissSuccess -> _state.update { it.copy(isSuccess = false, loanResult = null) }
        }
    }

    /**
     * Detects matching product locally to allow instant UI updates for MaxTenor and InterestRate.
     */
    private fun detectLocalPlafond(amount: Long) {
        if (allPlafonds.isEmpty()) {
            Timber.w("LoanSim: allPlafonds empty, will rely on API detection")
            // Don't return early - let the API call in triggerSimulation handle it
            return
        }
        
        // Find product where amount is within [minAmount, maxAmount]
        val detected = allPlafonds.find { amount >= it.minAmount && amount <= it.maxAmount }
        Timber.d("LoanSim: Local Detection for $amount -> Product: ${detected?.name}, RawMaxTenor: ${detected?.maxTenor}")
        
        if (detected == null) {
            Timber.w("LoanSim: No matching plafond found for amount $amount")
        }
        
        // Update state regardless of whether we found one or not (to clear stale if needed)
        // If detected is null -> we clear it, resetting maxTenor to default (60)
        _state.update { currentState ->
            var newTenor = currentState.tenor
            
            if (detected != null) {
                val maxTenor = if (detected.maxTenor > 0) detected.maxTenor else 60
                val currentTenorInt = currentState.tenor.toIntOrNull() ?: 0
                
                Timber.d("LoanSim: Detected plafond maxTenor logic -> raw: ${detected.maxTenor}, effective: $maxTenor, currentTenor: $currentTenorInt")
                
                // Auto-adjust tenor if it exceeds maxTenor
                if (currentTenorInt > maxTenor) {
                    newTenor = maxTenor.toString()
                    Timber.d("LoanSim: Auto-adjusted tenor from $currentTenorInt to $newTenor (max: $maxTenor)")
                } else {
                    Timber.d("LoanSim: Tenor $currentTenorInt is within limit $maxTenor, no adjustment needed")
                }
            }
            
            currentState.copy(
                selectedPlafond = detected,
                tenor = newTenor
            )
        }
    }

    /**
     * Calculates the simulation locally to provide instant feedback while the API is loading.
     * Uses the currently selected plafond's interest rate.
     */
    private fun calculateLocalSimulation() {
        val currentState = _state.value
        val amountStr = currentState.amount.replace("[^0-9]".toRegex(), "")
        val amountDouble = amountStr.toDoubleOrNull() ?: return
        val tenorInt = currentState.tenor.toIntOrNull() ?: return
        
        // Use current plafond rate or a default fallback if none selected yet (avoid 0 if possible)
        val rate = currentState.selectedPlafond?.interestRate ?: 0.0
        
        // Flat interest calculation: Principal + (Principal * Rate * Tenure/12)
        // Rate is usually yearly in percentage, so /100. If rate is monthly, logic changes.
        // Assuming rate is YEARLY PERCENTAGE based on typical loan apps.
        val totalInterest = amountDouble * (rate / 100) * (tenorInt.toDouble() / 12)
        val totalPayment = amountDouble + totalInterest
        val monthlyInstallment = totalPayment / tenorInt
        
        // Update state with a temporary simulation result
        _state.update { 
            it.copy(
                simulationResult = LoanSimulationResponse(
                    loanAmount = amountDouble,
                    tenor = tenorInt,
                    interestRate = rate,
                    totalInterest = totalInterest,
                    totalPayment = totalPayment,
                    monthlyInstallment = monthlyInstallment,
                    plafondId = it.selectedPlafond?.id,
                    plafondName = it.selectedPlafond?.name
                )
            )
        }
    }
    
    private fun triggerSimulation() {
        simulationJob?.cancel()
        simulationJob = viewModelScope.launch {
            delay(500) // Debounce
            
            val amountStr = _state.value.amount.replace("[^0-9]".toRegex(), "")
            val amountDouble = amountStr.toDoubleOrNull() ?: return@launch
            val amountLong = amountDouble.toLong()
            
            // Check if we already have a valid local detection
            val currentPlafond = _state.value.selectedPlafond
            
            if (currentPlafond != null) {
                Timber.d("LoanSim: Using locally detected plafond ${currentPlafond.name}, skipping API detection")
                simulate()
            } else {
                Timber.d("LoanSim: No local plafond found, attempting API detection")
                val plafondResult = safeApiCall { apiService.detectPlafond(amountLong) }
                
                if (plafondResult is Resource.Success && plafondResult.data != null) {
                    val detectedPlafond = plafondResult.data
                    Timber.d("LoanSim: API Detection -> ${detectedPlafond.name}")
                    
                    _state.update { currentState ->
                        var newTenor = currentState.tenor
                        val maxTenor = if (detectedPlafond.maxTenor > 0) detectedPlafond.maxTenor else 60
                        val currentTenorInt = currentState.tenor.toIntOrNull() ?: 0
                        
                        if (currentTenorInt > maxTenor) {
                            newTenor = maxTenor.toString()
                        }
                        
                        currentState.copy(
                            selectedPlafond = detectedPlafond,
                            tenor = newTenor
                        )
                    }
                    
                    simulate()
                } else {
                    Timber.d("LoanSim: API Detection failed or null")
                    _state.update { 
                        it.copy(
                            selectedPlafond = null, 
                            simulationResult = null,
                            simulationError = "Tidak ada produk pinjaman yang tersedia untuk jumlah ini."
                        ) 
                    }
                }
            }
        }
    }
    
    private fun simulate() {
        viewModelScope.launch {
            val currentState = _state.value
            val amountDouble = currentState.amount.replace("[^0-9]".toRegex(), "").toDoubleOrNull()
            val tenorInt = currentState.tenor.toIntOrNull()
            val plafondIdLong = currentState.selectedPlafond?.id
            
            if (amountDouble == null || tenorInt == null || plafondIdLong == null) {
                // Don't error explicitly here, just wait
                return@launch
            }
            
            // Only show loader if we don't have a local result to show, or maybe small indicator?
            // For now, let's NOT block the UI with a full loader, just small one
            _state.update { it.copy(isSimulating = true, simulationError = null) }
            
            val request = LoanSimulationRequest(
                amount = amountDouble,
                tenor = tenorInt,
                plafondId = plafondIdLong
            )
            
            val result = safeApiCall { apiService.simulateLoan(request) }
            
            when (result) {
                is Resource.Success -> {
                    _state.update { 
                        it.copy(
                            isSimulating = false, 
                            simulationResult = result.data,
                            simulationError = null
                        ) 
                    }
                }
                is Resource.Error -> {
                    Timber.e("Simulation failed: ${result.message}")
                    // Don't fully wipe result if we have local, but maybe show warning
                    _state.update { it.copy(isSimulating = false, simulationError = result.message) }
                }
                else -> _state.update { it.copy(isSimulating = false, simulationError = "Unknown error") }
            }
        }
    }
    
    /**
     * Shows confirmation dialog before applying for loan.
     * Only validates login status - profile validation is handled by the loan API.
     */
    private fun showConfirmDialog() {
        viewModelScope.launch {
            val currentState = _state.value
            
            // Check login status
            val isLoggedIn = tokenManager.isLoggedIn.first()
            if (!isLoggedIn) {
                _uiEvent.send(UiEvent.ShowSnackbar("Silakan login terlebih dahulu"))
                _uiEvent.send(UiEvent.Navigate("login"))
                return@launch
            }
            
            // Skip profile check - let the loan API handle validation
            // If profile is incomplete, the API will return an appropriate error
            
            val amountDouble = currentState.amount.replace("[^0-9]".toRegex(), "").toDoubleOrNull()
            val tenorInt = currentState.tenor.toIntOrNull()
            
            var hasError = false
            
            if (amountDouble == null || amountDouble <= 0) {
                _state.update { it.copy(amountError = "Masukkan jumlah pinjaman yang valid") }
                hasError = true
            }
            
            if (tenorInt == null || tenorInt <= 0) {
                _state.update { it.copy(tenorError = "Masukkan tenor yang valid") }
                hasError = true
            }
            
            // Validate against max tenor again just in case
            val maxTenor = currentState.selectedPlafond?.maxTenor ?: 0
            if (tenorInt != null && maxTenor > 0 && tenorInt > maxTenor) {
                _state.update { it.copy(tenorError = "Tenor melebihi batas maksimal ($maxTenor bulan)") }
                hasError = true
            }
            
            if (hasError) return@launch
            
            // Show confirmation dialog
            _state.update { it.copy(showConfirmDialog = true) }
        }
    }

    private fun applyLoan() {
        viewModelScope.launch {
            val currentState = _state.value
            
            val amountDouble = currentState.amount.replace("[^0-9]".toRegex(), "").toDoubleOrNull()
            val tenorInt = currentState.tenor.toIntOrNull()
            
            if (amountDouble == null || tenorInt == null) {
                _uiEvent.send(UiEvent.ShowSnackbar("Data tidak valid"))
                return@launch
            }
            
            _state.update { it.copy(isSubmitting = true) }
            
            // Use the new simplified request with only amount and tenorMonth
            val request = LoanApplicationRequest(
                amount = amountDouble,
                tenorMonth = tenorInt
            )
            
            val result = safeApiCall { apiService.applyLoan(request) }
            
            when (result) {
                is Resource.Success -> {
                    _state.update { 
                        it.copy(
                            isSubmitting = false, 
                            isSuccess = true,
                            loanResult = result.data
                        ) 
                    }
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

sealed class LoanSimulationEvent {
    data class AmountChanged(val value: String) : LoanSimulationEvent()
    data class TenorChanged(val value: String) : LoanSimulationEvent()
    data class PurposeChanged(val value: String) : LoanSimulationEvent()
    data object Simulate : LoanSimulationEvent()
    data object ShowConfirmDialog : LoanSimulationEvent()
    data object DismissConfirmDialog : LoanSimulationEvent()
    data object ConfirmApplyLoan : LoanSimulationEvent()
    data object ApplyLoan : LoanSimulationEvent()
    data object DismissSuccess : LoanSimulationEvent()
}
