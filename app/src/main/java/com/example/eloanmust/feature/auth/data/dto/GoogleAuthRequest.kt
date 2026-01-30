package com.example.eloanmust.feature.auth.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Google Auth request DTO - sends Firebase ID Token to backend
 */
data class GoogleAuthRequest(
    @SerializedName("idToken")
    val idToken: String,
    
    @SerializedName("fcmToken")
    val fcmToken: String? = null
)
