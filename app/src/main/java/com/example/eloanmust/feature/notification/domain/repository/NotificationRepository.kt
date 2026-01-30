package com.example.eloanmust.feature.notification.domain.repository

import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.data.model.Notification
import com.example.eloanmust.feature.notification.data.local.NotificationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Notification operations.
 * Follows offline-first architecture pattern.
 */
interface NotificationRepository {
    
    /**
     * Get all notifications for current user (from local database)
     * Returns Flow for reactive updates
     */
    fun getNotifications(): Flow<List<Notification>>
    
    /**
     * Get unread notifications count
     */
    fun getUnreadCount(): Flow<Int>
    
    /**
     * Refresh notifications from API and update local database
     */
    suspend fun refreshNotifications(): Resource<Unit>
    
    /**
     * Mark notification as read (both local and remote)
     */
    suspend fun markAsRead(notificationId: Long): Resource<Unit>
    
    /**
     * Mark all notifications as read
     */
    suspend fun markAllAsRead(): Resource<Unit>
    
    /**
     * Save notification to local database (called from FCM service)
     */
    suspend fun saveNotification(notification: NotificationEntity)
    
    /**
     * Clear all notifications for current user
     */
    suspend fun clearNotifications()
}
