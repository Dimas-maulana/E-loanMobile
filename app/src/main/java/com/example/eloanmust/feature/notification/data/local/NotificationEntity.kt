package com.example.eloanmust.feature.notification.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for Notification - matches api/notifications response
 */
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val id: Long,
    val userId: Long? = null,
    val loanApplicationId: Long? = null,
    val type: String,
    val channel: String? = null,
    val message: String,
    val isRead: Boolean = false,
    val createdAt: String? = null,
    val cachedAt: Long = System.currentTimeMillis()
)
