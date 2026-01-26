package com.example.eloanmust.feature.notification.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for Notification.
 */
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val id: Long,
    val userId: Long,
    val title: String,
    val message: String,
    val type: String,
    val referenceId: Long?,
    val referenceType: String?,
    val isRead: Boolean,
    val readAt: Long?,
    val createdAt: Long,
    val cachedAt: Long = System.currentTimeMillis()
)
