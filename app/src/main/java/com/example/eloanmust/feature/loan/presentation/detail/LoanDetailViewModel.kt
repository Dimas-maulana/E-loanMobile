package com.example.eloanmust.feature.loan.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.core.common.UiEvent
import com.example.eloanmust.core.network.ApiService
import com.example.eloanmust.core.network.safeApiCall
import com.example.eloanmust.feature.loan.data.dto.LoanDto
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

data class LoanDetailState(
    val isLoading: Boolean = false,
    val loan: LoanDto? = null,
    val error: String? = null
)

@HiltViewModel
class LoanDetailViewModel @Inject constructor(
    private val apiService: ApiService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val loanId: Long = savedStateHandle.get<Long>("loanId") ?: 0L
    
    private val _state = MutableStateFlow(LoanDetailState())
    val state: StateFlow<LoanDetailState> = _state.asStateFlow()
    
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    
    init {
        loadLoanDetail()
    }
    
    private fun loadLoanDetail() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            val result = safeApiCall { apiService.getLoanById(loanId) }
            
            when (result) {
                is Resource.Success -> {
                    Timber.d("Loaded loan detail: ${result.data}")
                    _state.update { it.copy(isLoading = false, loan = result.data, error = null) }
                }
                is Resource.Error -> {
                    Timber.e("Failed to load loan detail: ${result.message}")
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                else -> _state.update { it.copy(isLoading = false) }
            }
        }
    }
    
    fun refresh() {
        loadLoanDetail()
    }
}
