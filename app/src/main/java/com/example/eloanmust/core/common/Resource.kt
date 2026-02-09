package com.example.eloanmust.core.common

/**
 * A sealed class representing the state of a resource/data operation.
 * This follows the recommended approach for handling API responses
 * with Loading, Success, and Error states.
 *
 * @param T The type of data being wrapped
 */
sealed class Resource<out T> {

    /**
     * Represents a successful operation with data
     */
    data class Success<T>(val data: T) : Resource<T>()

    /**
     * Represents a failed operation with error message
     */
    data class Error(
        val message: String,
        val code: Int? = null,
        val exception: Throwable? = null
    ) : Resource<Nothing>()

    /**
     * Represents an ongoing operation
     */
    data object Loading : Resource<Nothing>()

    /**
     * Represents an empty/idle state
     */
    data object Idle : Resource<Nothing>()

    /**
     * Returns true if this Resource is in Loading state
     */
    val isLoading: Boolean get() = this is Loading

    /**
     * Returns true if this Resource is in Success state
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * Returns true if this Resource is in Error state
     */
    val isError: Boolean get() = this is Error

    /**
     * Returns the data if Success, null otherwise
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    /**
     * Returns the error message if Error, null otherwise
     */
    fun errorMessageOrNull(): String? = when (this) {
        is Error -> message
        else -> null
    }

    /**
     * Maps the success data to another type
     */
    inline fun <R> map(transform: (T) -> R): Resource<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(message, code, exception)
        is Loading -> Loading
        is Idle -> Idle
    }

    /**
     * Executes the given block if this is Success
     */
    inline fun onSuccess(action: (T) -> Unit): Resource<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * Executes the given block if this is Error
     */
    inline fun onError(action: (String, Int?, Throwable?) -> Unit): Resource<T> {
        if (this is Error) action(message, code, exception)
        return this
    }

    /**
     * Executes the given block if this is Loading
     */
    inline fun onLoading(action: () -> Unit): Resource<T> {
        if (this is Loading) action()
        return this
    }

    companion object {
        /**
         * Creates a Success Resource
         */
        fun <T> success(data: T): Resource<T> = Success(data)

        /**
         * Creates an Error Resource
         */
        fun error(
            message: String,
            code: Int? = null,
            exception: Throwable? = null
        ): Resource<Nothing> = Error(message, code, exception)

        /**
         * Creates a Loading Resource
         */
        fun loading(): Resource<Nothing> = Loading

        /**
         * Creates an Idle Resource
         */
        fun idle(): Resource<Nothing> = Idle
    }
}

/**
 * Extension function to convert a nullable value to Resource
 */
fun <T> T?.toResource(
    errorMessage: String = "Data not found"
): Resource<T> = if (this != null) {
    Resource.Success(this)
} else {
    Resource.Error(errorMessage)
}

/**
 * Extension function to safely execute a block and return Resource
 */
inline fun <T> resourceOf(
    errorMessage: String = "An error occurred",
    block: () -> T
): Resource<T> = try {
    Resource.Success(block())
} catch (e: Exception) {
    Resource.Error(e.message ?: errorMessage, exception = e)
}

/**
 * Extension function to run a suspend block and return Resource
 */
suspend inline fun <T> safeResourceCall(
    errorMessage: String = "An error occurred",
    crossinline block: suspend () -> T
): Resource<T> = try {
    Resource.Success(block())
} catch (e: Exception) {
    Resource.Error(e.message ?: errorMessage, exception = e)
}
