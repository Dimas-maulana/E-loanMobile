package com.example.eloanmust.feature.notification.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Notification DTO from API
 */
data class NotificationDto(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("userId")
    val userId: Long,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("type")
    val type: String,
    
    @SerializedName("referenceId")
    val referenceId: Long?,
    
    @SerializedName("referenceType")
    val referenceType: String?,
    
    @SerializedName("isRead")
    val isRead: Boolean = false,
    
    @SerializedName("readAt")
    val readAt: String?,
    
    @SerializedName("createdAt")
    val createdAt: String
)

/**
 * Unread count response DTO
 */
data class UnreadCountResponse(
    @SerializedName("count")
    val count: Int
)
