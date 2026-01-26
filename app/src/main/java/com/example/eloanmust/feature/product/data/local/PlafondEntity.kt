package com.example.eloanmust.feature.product.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for Plafond (Loan Product).
 */
@Entity(tableName = "plafonds")
data class PlafondEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val description: String?,
    val minAmount: Double,
    val maxAmount: Double,
    val interestRate: Double,
    val maxTenor: Int,
    val isActive: Boolean,
    val createdAt: Long?,
    val cachedAt: Long = System.currentTimeMillis()
)
