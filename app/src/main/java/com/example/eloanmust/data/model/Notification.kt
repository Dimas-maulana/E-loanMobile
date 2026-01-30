package com.example.eloanmust.data.model

data class Notification(
    val id: Long,
    val userId: Long?,
    val loanApplicationId: Long?,
    val type: String,
    val channel: String?,
    val message: String,
    val isRead: Boolean,
    val createdAt: String?
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
}
