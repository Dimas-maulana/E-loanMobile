package com.example.eloanmust.core.network

import com.example.eloanmust.core.common.Resource
import com.google.gson.Gson
import retrofit2.Response
import timber.log.Timber

/**
 * Safe API call wrapper that handles exceptions and converts
 * Retrofit Response to Resource sealed class.
 */
suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<ApiResponse<T>>
): Resource<T> {
    return try {
        val response = apiCall()
        handleApiResponse(response)
    } catch (e: Exception) {
        Timber.e(e, "API call failed")
        handleException(e)
    }
}

/**
 * Handle API response and convert to Resource
 */
private fun <T> handleApiResponse(response: Response<ApiResponse<T>>): Resource<T> {
    return if (response.isSuccessful) {
        val body = response.body()
        if (body != null && body.success && body.data != null) {
            Resource.Success(body.data)
        } else if (body != null && body.success) {
            // Success but no data (for endpoints like logout)
            @Suppress("UNCHECKED_CAST")
            Resource.Success(Unit as T)
        } else {
            Resource.Error(
                message = body?.message ?: body?.error ?: "Unknown error occurred",
                code = response.code()
            )
        }
    } else {
        handleErrorResponse(response)
    }
}

/**
 * Handle error response from API
 */
private fun <T> handleErrorResponse(response: Response<ApiResponse<T>>): Resource<T> {
    val errorBody = response.errorBody()?.string()
    
    return try {
        if (!errorBody.isNullOrBlank()) {
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            Resource.Error(
                message = errorResponse.message ?: errorResponse.error ?: getDefaultErrorMessage(response.code()),
                code = response.code()
            )
        } else {
            Resource.Error(
                message = getDefaultErrorMessage(response.code()),
                code = response.code()
            )
        }
    } catch (e: Exception) {
        Timber.e(e, "Error parsing error response")
        Resource.Error(
            message = getDefaultErrorMessage(response.code()),
            code = response.code()
        )
    }
}

/**
 * Handle exceptions during API call
 */
private fun <T> handleException(e: Exception): Resource<T> {
    return when (e) {
        is java.net.UnknownHostException -> Resource.Error(
            message = "Tidak dapat terhubung ke server. Periksa koneksi internet Anda.",
            exception = e
        )
        is java.net.SocketTimeoutException -> Resource.Error(
            message = "Koneksi timeout. Silakan coba lagi.",
            exception = e
        )
        is java.net.ConnectException -> Resource.Error(
            message = "Gagal terhubung ke server. Silakan coba lagi nanti.",
            exception = e
        )
        is javax.net.ssl.SSLException -> Resource.Error(
            message = "Koneksi tidak aman. Pastikan Anda menggunakan jaringan yang aman.",
            exception = e
        )
        is java.io.IOException -> Resource.Error(
            message = "Terjadi kesalahan jaringan. Silakan coba lagi.",
            exception = e
        )
        else -> Resource.Error(
            message = e.message ?: "Terjadi kesalahan yang tidak diketahui",
            exception = e
        )
    }
}

/**
 * Get default error message based on HTTP status code
 */
private fun getDefaultErrorMessage(code: Int): String {
    return when (code) {
        400 -> "Permintaan tidak valid"
        401 -> "Sesi Anda telah berakhir. Silakan login kembali."
        403 -> "Anda tidak memiliki akses ke fitur ini"
        404 -> "Data tidak ditemukan"
        408 -> "Waktu permintaan habis"
        409 -> "Data sudah ada atau konflik"
        422 -> "Data yang dikirim tidak valid"
        429 -> "Terlalu banyak permintaan. Silakan tunggu sebentar."
        500 -> "Terjadi kesalahan pada server"
        502 -> "Server sedang tidak tersedia"
        503 -> "Layanan sedang dalam pemeliharaan"
        504 -> "Server tidak merespon"
        else -> "Terjadi kesalahan (Kode: $code)"
    }
}

/**
 * Extension to check if error is authentication error (401)
 */
fun Resource.Error.isAuthError(): Boolean = code == 401

/**
 * Extension to check if error is network error
 */
fun Resource.Error.isNetworkError(): Boolean = 
    exception is java.net.UnknownHostException ||
    exception is java.net.SocketTimeoutException ||
    exception is java.net.ConnectException
