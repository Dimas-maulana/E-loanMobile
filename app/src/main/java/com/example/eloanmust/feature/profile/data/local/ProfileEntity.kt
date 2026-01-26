package com.example.eloanmust.feature.profile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for Customer Profile.
 */
@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey
    val userId: Long,
    val fullName: String?,
    val nik: String?,
    val birthDate: Long?,
    val birthPlace: String?,
    val address: String?,
    val phoneNumber: String?,
    val occupation: String?,
    val monthlyIncome: Double?,
    val ktpImageUrl: String?,
    val isProfileComplete: Boolean,
    val createdAt: Long?,
    val updatedAt: Long?,
    val cachedAt: Long = System.currentTimeMillis()
)
