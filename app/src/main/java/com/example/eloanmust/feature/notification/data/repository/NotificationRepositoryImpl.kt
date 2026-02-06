package com.example.eloanmust.feature.notification.data.repository

import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.core.datastore.TokenManager
import com.example.eloanmust.core.database.dao.NotificationDao
import com.example.eloanmust.core.network.ApiService
import com.example.eloanmust.core.network.safeApiCall
import com.example.eloanmust.data.model.Notification
import com.example.eloanmust.feature.notification.data.local.NotificationEntity
import com.example.eloanmust.feature.notification.data.mapper.toDomain
import com.example.eloanmust.feature.notification.data.mapper.toEntity
import com.example.eloanmust.feature.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of NotificationRepository with offline-first strategy.
 * - Reads from local database (reactive)
 * - Syncs with API periodically
 * - Saves FCM notifications to local database
 */
@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val notificationDao: NotificationDao,
    private val tokenManager: TokenManager
) : NotificationRepository {
    
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun getNotifications(): Flow<List<Notification>> {
        // Fetch ALL notifications and filter in memory to debug visibility issues
        // This ensures we can see if data exists even if userId query is failing
        return notificationDao.getAllNotifications().flatMapLatest { entities ->
            tokenManager.userId.map { userId ->
                val currentId = userId ?: 0L
                Timber.d("NotificationRepository: Filtering ${entities.size} entities for userId: $currentId")
                
                val filtered = if (currentId == 0L) {
                     // Fallback: If no user logged in (or id 0), show all (dev/debug safety)
                     entities 
                } else {
                     entities.filter { it.userId == currentId }
                }
                
                Timber.d("NotificationRepository: Returning ${filtered.size} notifications after filter")
                filtered.map { it.toDomain() }
            }
        }
    }
    
    override fun getUnreadCount(): Flow<Int> {
        return tokenManager.userId.flatMapLatest { userId ->
            val id = userId ?: 0L
            notificationDao.getUnreadCount(id)
        }
    }
    
    override suspend fun refreshNotifications(): Resource<Unit> {
        val currentUserId = getUserId()
        
        // Even if userId is 0, we try to fetch if we have a token (safeApiCall handles 401)
        Timber.d("Notification: Refreshing for currentUserId: $currentUserId")
        
        val result = safeApiCall { apiService.getNotifications() }
        
        return when (result) {
            is Resource.Success -> {
                val notifications = result.data ?: emptyList()
                
                Timber.d("Notification: Received ${notifications.size} notifications from API")
                
                if (notifications.isNotEmpty()) {
                    // Use currentUserId to ensure data is accessible by the local query
                    // We prioritize the local session ID because the API call is authenticated for this user
                    val entities = notifications.map { dto ->
                        val effectiveUserId = if (currentUserId > 0) currentUserId else (dto.userId ?: 0L)
                        dto.toEntity(effectiveUserId)
                    }
                    
                    Timber.d("Notification: Saving ${entities.size} notifications to database for user $currentUserId")
                    notificationDao.insertNotifications(entities)
                }
                
                Resource.Success(Unit)
            }
            is Resource.Error -> {
                Timber.e("Notification: Failed to sync from API: ${result.message}")
                Resource.Error(result.message ?: "Unknown error")
            }
            else -> {
                // Handle Loading or other states
                Resource.Error("Unexpected state")
            }
        }
    }
    
    override suspend fun markAsRead(notificationId: Long): Resource<Unit> {
        // Update local first (optimistic update)
        notificationDao.markAsRead(notificationId)
        
        // Sync with API in background
        val result = safeApiCall { apiService.markNotificationAsRead(notificationId) }
        
        if (result is Resource.Error) {
            Timber.e("Notification: Failed to mark as read on API: ${result.message}")
            // Keep local update, API will sync later
        }
        
        return Resource.Success(Unit)
    }
    
    override suspend fun markAllAsRead(): Resource<Unit> {
        val userId = getUserId()
        
        // Update local first
        notificationDao.markAllAsRead(userId)
        
        // Sync with API
        val result = safeApiCall { apiService.markAllNotificationsAsRead() }
        
        if (result is Resource.Error) {
            Timber.e("Notification: Failed to mark all as read on API: ${result.message}")
        }
        
        return Resource.Success(Unit)
    }
    
    override suspend fun saveNotification(notification: NotificationEntity) {
        notificationDao.insertNotification(notification)
        Timber.d("Notification: Saved notification ID: ${notification.id}")
    }
    
    override suspend fun clearNotifications() {
        val userId = getUserId()
        notificationDao.clearNotificationsForUser(userId)
        Timber.d("Notification: Cleared all notifications for user: $userId")
    }
    
    /**
     * Get user ID from token manager
     */
    private suspend fun getUserId(): Long {
        return tokenManager.userId.first() ?: 0L
    }
}
