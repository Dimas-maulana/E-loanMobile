package com.example.eloanmust.feature.notification.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.core.common.UiEvent
import com.example.eloanmust.data.model.Notification
import com.example.eloanmust.feature.notification.domain.repository.NotificationRepository
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

data class NotificationState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoggedIn: Boolean = false,
    val notifications: List<Notification> = emptyList(),
    val unreadCount: Int = 0,
    val error: String? = null
)

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val tokenManager: com.example.eloanmust.core.datastore.TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationState())
    val state: StateFlow<NotificationState> = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        checkLoginAndLoadNotifications()
    }

    private fun checkLoginAndLoadNotifications() {
        viewModelScope.launch {
            try {
                // Observe login state
                tokenManager.isLoggedIn.collect { isLoggedIn ->
                    _state.update { it.copy(isLoggedIn = isLoggedIn) }

                    if (isLoggedIn) {
                        // 1. Start observing DB immediately (Offline First)
                        launch {
                            notificationRepository.getNotifications().collect { notifications ->
                                Timber.d("NotificationViewModel: Collected ${notifications.size} notifications from DB")
                                val unreadCount = notifications.count { !it.isRead }
                                _state.update {
                                    it.copy(
                                        notifications = notifications,
                                        unreadCount = unreadCount,
                                        isLoading = false // Stop loading once we have data or empty list from DB
                                    )
                                }
                            }
                        }

                        // 2. Trigger API refresh in parallel
                        launch {
                            refreshNotificationsFromApi()
                        }
                    } else {
                        Timber.d("NotificationViewModel: User not logged in")
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error in checkLoginAndLoadNotifications")
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private suspend fun refreshNotificationsFromApi() {
        _state.update { it.copy(isLoading = true) }

        val result = notificationRepository.refreshNotifications()

        when (result) {
            is Resource.Success -> {
                Timber.d("NotificationViewModel: Refresh successful")
                _state.update { it.copy(isLoading = false, error = null) }
            }
            is Resource.Error -> {
                Timber.e("NotificationViewModel: Failed to load notifications: ${result.message}")
                _state.update { it.copy(isLoading = false, error = result.message) }
            }
            else -> _state.update { it.copy(isLoading = false) }
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = notificationRepository.refreshNotifications()

            when (result) {
                is Resource.Success -> {
                    Timber.d("NotificationViewModel: Load successful")
                    _state.update { it.copy(isLoading = false, error = null) }
                }
                is Resource.Error -> {
                    Timber.e("Failed to load notifications: ${result.message}")
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                else -> _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }

            val result = notificationRepository.refreshNotifications()

            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(isRefreshing = false, error = null) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isRefreshing = false) }
                    _uiEvent.send(UiEvent.ShowSnackbar(result.message))
                }
                else -> _state.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun markAsRead(notificationId: Long) {
        viewModelScope.launch {
            notificationRepository.markAsRead(notificationId)
            // Local state will be updated automatically via Flow from repository
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            val result = notificationRepository.markAllAsRead()

            if (result is Resource.Success) {
                _uiEvent.send(UiEvent.ShowSnackbar("Semua notifikasi ditandai sudah dibaca"))
            }
            // Local state will be updated automatically via Flow from repository
        }
    }
}
