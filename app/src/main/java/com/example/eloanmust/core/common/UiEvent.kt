package com.example.eloanmust.core.common

/**
 * Sealed class representing one-time UI events.
 * Used for showing snackbars, toasts, navigation, etc.
 */
sealed class UiEvent {
    /**
     * Navigate to a specific route
     */
    data class Navigate(val route: String) : UiEvent()
    
    /**
     * Navigate back
     */
    data object NavigateBack : UiEvent()
    
    /**
     * Show a snackbar message
     */
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null,
        val duration: SnackbarDuration = SnackbarDuration.Short
    ) : UiEvent()
    
    /**
     * Show a toast message
     */
    data class ShowToast(val message: String) : UiEvent()
    
    /**
     * Show an error dialog
     */
    data class ShowErrorDialog(
        val title: String = "Error",
        val message: String
    ) : UiEvent()
    
    /**
     * Show a success dialog
     */
    data class ShowSuccessDialog(
        val title: String = "Success",
        val message: String
    ) : UiEvent()
    
    /**
     * Show a loading overlay
     */
    data class ShowLoading(val isLoading: Boolean) : UiEvent()
    
    /**
     * Scroll to top of the list
     */
    data object ScrollToTop : UiEvent()
    
    /**
     * Hide keyboard
     */
    data object HideKeyboard : UiEvent()
    
    /**
     * Open external URL
     */
    data class OpenUrl(val url: String) : UiEvent()
    
    /**
     * Share content
     */
    data class Share(
        val text: String,
        val title: String? = null
    ) : UiEvent()
    
    /**
     * Call phone number
     */
    data class CallPhone(val phoneNumber: String) : UiEvent()
    
    /**
     * Send email
     */
    data class SendEmail(
        val email: String,
        val subject: String? = null,
        val body: String? = null
    ) : UiEvent()
    
    /**
     * Logout user
     */
    data object Logout : UiEvent()
    
    /**
     * Session expired
     */
    data object SessionExpired : UiEvent()
}

/**
 * Snackbar duration enum (maps to Compose duration)
 */
enum class SnackbarDuration {
    Short,
    Long,
    Indefinite
}
