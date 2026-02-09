package com.example.eloanmust.feature.product.domain.repository

import com.example.eloanmust.core.common.Resource
import com.example.eloanmust.feature.product.data.dto.PlafondDto
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Plafond (Loan Product) operations.
 * Provides offline-first access to plafond data.
 */
interface PlafondRepository {

    /**
     * Get all active plafonds with offline-first strategy.
     * 1. Emit cached data from Room first
     * 2. Fetch fresh data from API
     * 3. Update cache
     * 4. Emit fresh data
     */
    fun getPlafonds(): Flow<Resource<List<PlafondDto>>>

    /**
     * Get all cached plafonds synchronously.
     * Useful for local detection without network call.
     */
    suspend fun getCachedPlafonds(): List<PlafondDto>

    /**
     * Force refresh plafonds from API and update cache.
     */
    suspend fun refreshPlafonds(): Resource<Unit>

    /**
     * Find plafond for given amount from cache.
     * Returns null if no matching plafond found.
     */
    suspend fun findPlafondForAmount(amount: Long): PlafondDto?

    /**
     * Detect plafond for amount - first try cache, then API.
     */
    suspend fun detectPlafond(amount: Long): Resource<PlafondDto>
}
