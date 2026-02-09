package com.example.eloanmust.core.common

/**
 * Represents the UI state for a screen.
 * Contains all necessary information for rendering the UI.
 *
 * @param T The type of data being displayed
 */
data class UiState<out T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val error: String? = null,
    val isRefreshing: Boolean = false
) {
    /**
     * Returns true if there's valid data to display
     */
    val hasData: Boolean get() = data != null

    /**
     * Returns true if there's an error to display
     */
    val hasError: Boolean get() = error != null

    /**
     * Returns true if the state is in a successful state with data
     */
    val isSuccess: Boolean get() = !isLoading && data != null && error == null

    /**
     * Returns true if this is the initial loading state
     */
    val isInitialLoading: Boolean get() = isLoading && data == null

    companion object {
        /**
         * Creates an initial/idle state
         */
        fun <T> idle(): UiState<T> = UiState()

        /**
         * Creates a loading state
         */
        fun <T> loading(existingData: T? = null): UiState<T> = UiState(
            isLoading = true,
            data = existingData
        )

        /**
         * Creates a success state with data
         */
        fun <T> success(data: T): UiState<T> = UiState(
            isLoading = false,
            data = data,
            error = null
        )

        /**
         * Creates an error state
         */
        fun <T> error(message: String, existingData: T? = null): UiState<T> = UiState(
            isLoading = false,
            data = existingData,
            error = message
        )

        /**
         * Creates a refreshing state (pull-to-refresh)
         */
        fun <T> refreshing(existingData: T? = null): UiState<T> = UiState(
            isLoading = false,
            data = existingData,
            isRefreshing = true
        )
    }
}

/**
 * Extension to update UiState from Resource
 */
fun <T> UiState<T>.fromResource(resource: Resource<T>): UiState<T> = when (resource) {
    is Resource.Loading -> copy(isLoading = true, error = null)
    is Resource.Success -> UiState.success(resource.data)
    is Resource.Error -> copy(isLoading = false, error = resource.message)
    is Resource.Idle -> UiState.idle()
}

/**
 * Extension to map UiState data
 */
inline fun <T, R> UiState<T>.map(transform: (T) -> R): UiState<R> = UiState(
    isLoading = isLoading,
    data = data?.let(transform),
    error = error,
    isRefreshing = isRefreshing
)
