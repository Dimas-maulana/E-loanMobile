package com.example.eloanmust.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.core.common.UiEvent
import com.example.eloanmust.core.datastore.TokenManager
import com.example.eloanmust.feature.product.data.dto.PlafondDto
import com.example.eloanmust.feature.product.domain.repository.PlafondRepository
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

data class HomeState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val username: String = "User",
    val products: List<PlafondDto> = emptyList(),
    val unreadNotificationCount: Int = 0,
    val error: String? = null,
    val isOffline: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val plafondRepository: PlafondRepository,
    private val tokenManager: TokenManager,
    private val notificationRepository: com.example.eloanmust.feature.notification.domain.repository.NotificationRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()
    
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    
    init {
        checkLoginStatus()
        loadProducts()
        observeUnreadNotifications()
    }
    
    private fun checkLoginStatus() {
        viewModelScope.launch {
            val isLoggedIn = tokenManager.isLoggedIn.first()
            val username = tokenManager.username.first() ?: "User"
            _state.update { it.copy(isLoggedIn = isLoggedIn, username = username) }
        }
    }
    
    private fun observeUnreadNotifications() {
        viewModelScope.launch {
            try {
                val isLoggedIn = tokenManager.isLoggedIn.first()
                if (isLoggedIn) {
                    notificationRepository.getUnreadCount().collect { count ->
                        _state.update { it.copy(unreadNotificationCount = count) }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error observing unread notifications")
                // Don't crash, just ignore the error
            }
        }
    }
    
    /**
     * Load products with offline-first strategy.
     * 1. Show cached products immediately
     * 2. Fetch fresh data from API if online
     * 3. Update UI with fresh data
     */
    private fun loadProducts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            plafondRepository.getPlafonds().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Timber.d("Home: Loaded ${result.data.size} products")
                        _state.update { 
                            it.copy(
                                isLoading = false, 
                                products = result.data, 
                                error = null,
                                isOffline = false
                            ) 
                        }
                    }
                    is Resource.Error -> {
                        Timber.e("Home: Failed to load products: ${result.message}")
                        _state.update { 
                            it.copy(
                                isLoading = false, 
                                error = result.message,
                                isOffline = result.message?.contains("koneksi") == true
                            ) 
                        }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    else -> {
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
    }
    
    fun refresh() {
        checkLoginStatus()
        loadProducts()
    }
    
    fun onApplyLoan(onLoginRequired: () -> Unit, onProceed: () -> Unit) {
        viewModelScope.launch {
            val isLoggedIn = tokenManager.isLoggedIn.first()
            if (isLoggedIn) {
                onProceed()
            } else {
                _uiEvent.send(UiEvent.ShowSnackbar("Silakan login terlebih dahulu"))
                onLoginRequired()
            }
        }
    }
}
