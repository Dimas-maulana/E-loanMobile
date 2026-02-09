package com.example.eloanmust.core.common

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Patterns
import java.io.InputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ============================================
// STRING EXTENSIONS
// ============================================

/**
 * Check if string is a valid email
 */
fun String.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Check if string is a valid phone number
 */
fun String.isValidPhoneNumber(): Boolean {
    return Patterns.PHONE.matcher(this).matches() &&
        this.length >= Constants.Validation.PHONE_MIN_LENGTH &&
        this.length <= Constants.Validation.PHONE_MAX_LENGTH
}

/**
 * Check if string is a valid NIK (16 digits)
 */
fun String.isValidNik(): Boolean {
    return this.length == Constants.Validation.NIK_LENGTH && this.all { it.isDigit() }
}

/**
 * Check if string is a valid password
 */
fun String.isValidPassword(): Boolean {
    return this.length >= Constants.Validation.MIN_PASSWORD_LENGTH &&
        this.length <= Constants.Validation.MAX_PASSWORD_LENGTH
}

/**
 * Mask email for display (e.g., j***@gmail.com)
 */
fun String.maskEmail(): String {
    if (!this.isValidEmail()) return this
    val parts = this.split("@")
    val name = parts[0]
    val domain = parts[1]
    val maskedName = if (name.length > 2) {
        "${name.first()}${"*".repeat(name.length - 2)}${name.last()}"
    } else {
        "${name.first()}*"
    }
    return "$maskedName@$domain"
}

/**
 * Mask phone number for display (e.g., 08***678)
 */
fun String.maskPhoneNumber(): String {
    if (this.length < 8) return this
    return "${this.take(4)}${"*".repeat(this.length - 7)}${this.takeLast(3)}"
}

/**
 * Capitalize first letter of each word
 */
fun String.toTitleCase(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { it.uppercase() }
    }
}

/**
 * Get initials from name (e.g., "John Doe" -> "JD")
 */
fun String.getInitials(maxLength: Int = 2): String {
    return this.split(" ")
        .take(maxLength)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")
}

// ============================================
// NUMBER/CURRENCY EXTENSIONS
// ============================================

/**
 * Format number as Indonesian Rupiah currency
 */
fun Number.toRupiah(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(this)
}

/**
 * Format number as currency without symbol
 */
fun Number.formatCurrency(): String {
    val format = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return format.format(this)
}

/**
 * Format number with thousands separator
 */
fun Number.formatWithSeparator(): String {
    return NumberFormat.getNumberInstance(Locale("id", "ID")).format(this)
}

/**
 * Format as percentage
 */
fun Number.toPercentage(decimals: Int = 2): String {
    return String.format(Locale.US, "%.${decimals}f%%", this.toDouble())
}

/**
 * Parse currency string to Double
 */
fun String.parseCurrency(): Double {
    return this.replace(Regex("[^0-9]"), "").toDoubleOrNull() ?: 0.0
}

// ============================================
// DATE EXTENSIONS
// ============================================

/**
 * Format date to display string
 */
fun Date.format(pattern: String = "dd MMM yyyy"): String {
    val formatter = SimpleDateFormat(pattern, Locale("id", "ID"))
    return formatter.format(this)
}

/**
 * Format date with time
 */
fun Date.formatWithTime(): String {
    return this.format("dd MMM yyyy, HH:mm")
}

/**
 * Format as relative time (e.g., "2 hours ago")
 */
fun Date.toRelativeTime(): String {
    val now = System.currentTimeMillis()
    val diff = now - this.time

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "Baru saja"
        minutes < 60 -> "$minutes menit lalu"
        hours < 24 -> "$hours jam lalu"
        days < 7 -> "$days hari lalu"
        else -> this.format("dd MMM yyyy")
    }
}

/**
 * Parse date string to Date object
 */
fun String.toDate(pattern: String = "yyyy-MM-dd'T'HH:mm:ss"): Date? {
    return try {
        SimpleDateFormat(pattern, Locale.getDefault()).parse(this)
    } catch (e: Exception) {
        null
    }
}

// ============================================
// CONTEXT EXTENSIONS
// ============================================

/**
 * Convert URI to Base64 string
 */
fun Context.uriToBase64(uri: Uri): String? {
    return try {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        bytes?.let { Base64.encodeToString(it, Base64.DEFAULT) }
    } catch (e: Exception) {
        null
    }
}

/**
 * Get file size from URI
 */
fun Context.getFileSize(uri: Uri): Long {
    return try {
        val cursor = contentResolver.query(uri, null, null, null, null)
        val sizeIndex = cursor?.getColumnIndex(android.provider.OpenableColumns.SIZE) ?: -1
        cursor?.moveToFirst()
        val size = if (sizeIndex >= 0) cursor?.getLong(sizeIndex) ?: 0L else 0L
        cursor?.close()
        size
    } catch (e: Exception) {
        0L
    }
}

// ============================================
// COLLECTION EXTENSIONS
// ============================================

/**
 * Safe get with null check
 */
fun <T> List<T>.getOrDefault(index: Int, default: T): T {
    return if (index in indices) this[index] else default
}

/**
 * Update item in list immutably
 */
inline fun <T> List<T>.updateAt(index: Int, update: (T) -> T): List<T> {
    return this.mapIndexed { i, item ->
        if (i == index) update(item) else item
    }
}

// ============================================
// LOAN STATUS EXTENSIONS
// ============================================

/**
 * Get display name for loan status
 */
fun String.toLoanStatusDisplayName(): String {
    return when (this.uppercase()) {
        Constants.LoanStatus.PENDING_REVIEW -> "Menunggu Review"
        Constants.LoanStatus.REVIEWED -> "Sedang Ditinjau"
        Constants.LoanStatus.APPROVED -> "Disetujui"
        Constants.LoanStatus.REJECTED -> "Ditolak"
        Constants.LoanStatus.DISBURSED -> "Dana Cair"
        else -> this
    }
}

/**
 * Get icon name for loan status
 */
fun String.toLoanStatusIcon(): String {
    return when (this.uppercase()) {
        Constants.LoanStatus.PENDING_REVIEW -> "schedule"
        Constants.LoanStatus.REVIEWED -> "rate_review"
        Constants.LoanStatus.APPROVED -> "check_circle"
        Constants.LoanStatus.REJECTED -> "cancel"
        Constants.LoanStatus.DISBURSED -> "payments"
        else -> "info"
    }
}
