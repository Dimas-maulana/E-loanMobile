package com.example.eloanmust.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eloanmust.feature.notification.data.local.NotificationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Notification entity.
 */
@Dao
interface NotificationDao {
    
    /**
     * Get all notifications, ordered by created date (newest first)
     */
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getNotifications(userId: Long): Flow<List<NotificationEntity>>
    
    /**
     * Get all notifications regardless of userId (for debugging/fallback)
     */
    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>
    
    /**
     * Get all notifications sync (for debugging)
     */
    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    suspend fun getAllNotificationsSync(): List<NotificationEntity>
    
    /**
     * Get all notifications (sync)
     */
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getNotificationsSync(userId: Long): List<NotificationEntity>
    
    /**
     * Get unread notifications
     */
    @Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 0 ORDER BY createdAt DESC")
    fun getUnreadNotifications(userId: Long): Flow<List<NotificationEntity>>
    
    /**
     * Get unread count
     */
    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    fun getUnreadCount(userId: Long): Flow<Int>
    
    /**
     * Get all unread count regardless of userId (for debugging/fallback)
     */
    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getAllUnreadCount(): Flow<Int>
    
    /**
     * Get unread count (sync)
     */
    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    suspend fun getUnreadCountSync(userId: Long): Int
    
    /**
     * Get notification by ID
     */
    @Query("SELECT * FROM notifications WHERE id = :notificationId LIMIT 1")
    suspend fun getNotificationById(notificationId: Long): NotificationEntity?
    
    /**
     * Insert or update notification
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)
    
    /**
     * Insert multiple notifications
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)
    
    /**
     * Mark notification as read
     */
    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markAsRead(notificationId: Long)
    
    /**
     * Mark all notifications as read
     */
    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId AND isRead = 0")
    suspend fun markAllAsRead(userId: Long)
    
    /**
     * Delete notification by ID
     */
    @Query("DELETE FROM notifications WHERE id = :notificationId")
    suspend fun deleteNotification(notificationId: Long)
    
    /**
     * Clear all notifications for user
     */
    @Query("DELETE FROM notifications WHERE userId = :userId")
    suspend fun clearNotificationsForUser(userId: Long)
    
    /**
     * Clear all notifications
     */
    @Query("DELETE FROM notifications")
    suspend fun clearAll()
}
