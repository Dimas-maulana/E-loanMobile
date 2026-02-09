package com.example.eloanmust.feature.notification.data.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO for notification data - matches api/notifications response
 */
data class NotificationDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("userId")
    val userId: Long? = null,

    @SerializedName("loanApplicationId")
    val loanApplicationId: Long? = null,

    @SerializedName("type")
    val type: String,

    @SerializedName("channel")
    val channel: String? = null,

    @SerializedName("message")
    val message: String,

    @SerializedName("isRead")
    val isRead: Boolean = false,

    @SerializedName("createdAt")
    val createdAt: String? = null
) {
    /**
     * Generate title from notification type
     */
    fun getTitle(): String {
        return when (type) {
            "LOAN_SUBMITTED" -> "Pengajuan Diterima"
            "LOAN_IN_REVIEW" -> "Sedang Ditinjau"
            "LOAN_REVIEWED" -> "Telah Ditinjau"
            "LOAN_APPROVED" -> "Pinjaman Disetujui"
            "LOAN_REJECTED" -> "Pinjaman Ditolak"
            "LOAN_DISBURSED" -> "Dana Dicairkan"
            "APPLICATION_SUBMITTED" -> "Pengajuan Diterima"
            "APPLICATION_REVIEWED" -> "Telah Ditinjau"
            "APPLICATION_APPROVED" -> "Pinjaman Disetujui"
            "APPLICATION_REJECTED" -> "Pinjaman Ditolak"
            "APPLICATION_DISBURSED" -> "Dana Dicairkan"
            else -> "Notifikasi"
        }
    }

    /**
     * Get loan ID for navigation (alias for loanApplicationId)
     */
    fun getLoanId(): Long? = loanApplicationId
}

/**
 * DTO for unread count response
 */
data class UnreadCountResponse(
    @SerializedName("count")
    val count: Int
)
