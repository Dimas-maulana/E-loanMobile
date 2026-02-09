package com.example.eloanmust.feature.loan.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.core.common.UiEvent
import com.example.eloanmust.core.database.dao.LoanDao
import com.example.eloanmust.core.datastore.TokenManager
import com.example.eloanmust.core.network.ApiService
import com.example.eloanmust.core.network.safeApiCall
import com.example.eloanmust.feature.loan.data.dto.LoanDto
import com.example.eloanmust.feature.loan.data.mapper.toEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class LoanHistoryState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoggedIn: Boolean = false,
    val loans: List<LoanDto> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class LoanHistoryViewModel @Inject constructor(
    private val apiService: ApiService,
    private val loanDao: LoanDao,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(LoanHistoryState())
    val state: StateFlow<LoanHistoryState> = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        checkLoginAndLoadLoans()
    }

    private fun checkLoginAndLoadLoans() {
        viewModelScope.launch {
            val isLoggedIn = tokenManager.isLoggedIn.first()
            _state.update { it.copy(isLoggedIn = isLoggedIn) }

            if (isLoggedIn) {
                loadLoans()
            }
        }
    }

    fun loadLoans() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // First, try to load from local cache
            val userId = tokenManager.userId.first()
            if (userId != null) {
                val cachedLoans = loanDao.getLoansByUserIdSync(userId)
                if (cachedLoans.isNotEmpty()) {
                    // Convert entities to DTOs for display (simplified)
                    Timber.d("Loaded ${cachedLoans.size} loans from cache")
                }
            }

            // Then fetch from API
            val result = safeApiCall { apiService.getMyLoans() }

            when (result) {
                is Resource.Success -> {
                    Timber.d("Loaded ${result.data.size} loans from API")
                    _state.update { it.copy(isLoading = false, loans = result.data, error = null) }

                    // Cache to local database (Offline-First)
                    if (userId != null) {
                        val entities = result.data.map { it.toEntity(userId) }
                        loanDao.insertLoans(entities)
                    }
                }
                is Resource.Error -> {
                    Timber.e("Failed to load loans: ${result.message}")
                    _state.update { it.copy(isLoading = false, error = result.message) }
                    _uiEvent.send(UiEvent.ShowSnackbar(result.message))
                }
                else -> _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }

            val result = safeApiCall { apiService.getMyLoans() }

            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(isRefreshing = false, loans = result.data, error = null) }

                    val userId = tokenManager.userId.first()
                    if (userId != null) {
                        val entities = result.data.map { it.toEntity(userId) }
                        loanDao.deleteByUserId(userId)
                        loanDao.insertLoans(entities)
                    }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isRefreshing = false) }
                    _uiEvent.send(UiEvent.ShowSnackbar(result.message))
                }
                else -> _state.update { it.copy(isRefreshing = false) }
            }
        }
    }
}
