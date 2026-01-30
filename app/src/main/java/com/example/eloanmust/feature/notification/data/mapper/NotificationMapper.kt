package com.example.eloanmust.feature.notification.data.mapper

import com.example.eloanmust.data.model.Notification
import com.example.eloanmust.feature.notification.data.dto.NotificationDto
import com.example.eloanmust.feature.notification.data.local.NotificationEntity
import com.google.firebase.messaging.RemoteMessage

/**
 * Mapper for Notification data conversions - updated to match api/notifications response
 */

/**
 * Convert NotificationDto to NotificationEntity
 */
fun NotificationDto.toEntity(): NotificationEntity {
    return NotificationEntity(
        id = this.id,
        userId = this.userId,
        loanApplicationId = this.loanApplicationId,
        type = this.type,
        channel = this.channel,
        message = this.message,
        isRead = this.isRead,
        createdAt = this.createdAt,
        cachedAt = System.currentTimeMillis()
    )
}

/**
 * Convert NotificationDto to NotificationEntity with explicit userId
 */
fun NotificationDto.toEntity(userId: Long): NotificationEntity {
    return this.toEntity().copy(userId = userId)
}

/**
 * Convert NotificationEntity to NotificationDto
 */
fun NotificationEntity.toDto(): NotificationDto {
    return NotificationDto(
        id = this.id,
        userId = this.userId,
        loanApplicationId = this.loanApplicationId,
        type = this.type,
        channel = this.channel,
        message = this.message,
        isRead = this.isRead,
        createdAt = this.createdAt
    )
}

/**
 * Convert NotificationEntity to Domain Model
 */
fun NotificationEntity.toDomain(): Notification {
    return Notification(
        id = this.id,
        userId = this.userId,
        loanApplicationId = this.loanApplicationId,
        type = this.type,
        channel = this.channel,
        message = this.message,
        isRead = this.isRead,
        createdAt = this.createdAt
    )
}

/**
 * Convert FCM RemoteMessage to NotificationEntity
 */
fun RemoteMessage.toNotificationEntity(userId: Long): NotificationEntity {
    val data = this.data
    
    // Generate current timestamp in ISO format for createdAt
    val currentTime = java.time.Instant.now().toString()
    
    return NotificationEntity(
        id = data["notificationId"]?.toLongOrNull() ?: System.currentTimeMillis(),
        userId = userId,
        loanApplicationId = data["loanApplicationId"]?.toLongOrNull() ?: data["loanId"]?.toLongOrNull(),
        type = data["notificationType"] ?: "SYSTEM",
        channel = data["channel"] ?: "PUSH",
        message = data["body"] ?: this.notification?.body ?: "",
        isRead = false,
        createdAt = currentTime,
        cachedAt = System.currentTimeMillis()
    )
}

/**
 * Convert list of NotificationEntity to list of NotificationDto
 */
fun List<NotificationEntity>.toDtoList(): List<NotificationDto> = map { it.toDto() }

/**
 * Convert list of NotificationDto to list of NotificationEntity
 */
fun List<NotificationDto>.toEntityList(userId: Long? = null): List<NotificationEntity> = map {
    if (userId != null) it.toEntity(userId) else it.toEntity()
}
