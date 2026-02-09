package com.example.eloanmust.feature.product.data.datasource

import com.example.eloanmust.core.database.dao.PlafondDao
import com.example.eloanmust.feature.product.data.local.PlafondEntity
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local data source for Plafond entities.
 * Wraps PlafondDao for data access operations.
 */
@Singleton
class PlafondLocalDataSource @Inject constructor(
    private val plafondDao: PlafondDao
) {

    /**
     * Get all active plafonds as Flow for reactive updates.
     */
    fun getActivePlafonds(): Flow<List<PlafondEntity>> {
        return plafondDao.getActivePlafonds()
    }

    /**
     * Get all active plafonds synchronously.
     */
    suspend fun getActivePlafondsSync(): List<PlafondEntity> {
        return plafondDao.getActivePlafondsSync()
    }

    /**
     * Get plafond by ID.
     */
    suspend fun getPlafondById(plafondId: Long): PlafondEntity? {
        return plafondDao.getPlafondById(plafondId)
    }

    /**
     * Find plafond for given amount (where amount is within min-max range).
     */
    suspend fun findPlafondForAmount(amount: Double): PlafondEntity? {
        return plafondDao.findPlafondForAmount(amount)
    }

    /**
     * Insert or update a single plafond.
     */
    suspend fun insertPlafond(plafond: PlafondEntity) {
        Timber.d("Inserting plafond: ${plafond.name}")
        plafondDao.insertPlafond(plafond)
    }

    /**
     * Insert or update multiple plafonds.
     */
    suspend fun insertPlafonds(plafonds: List<PlafondEntity>) {
        Timber.d("Inserting ${plafonds.size} plafonds to cache")
        plafondDao.insertPlafonds(plafonds)
    }

    /**
     * Delete plafond by ID.
     */
    suspend fun deletePlafond(plafondId: Long) {
        plafondDao.deletePlafond(plafondId)
    }

    /**
     * Clear all cached plafonds.
     */
    suspend fun clearAll() {
        Timber.d("Clearing all cached plafonds")
        plafondDao.clearAll()
    }

    /**
     * Get last cache timestamp to check if cache is stale.
     */
    suspend fun getLastCacheTime(): Long? {
        return plafondDao.getLastCacheTime()
    }

    /**
     * Check if cache exists.
     */
    suspend fun hasCache(): Boolean {
        return getActivePlafondsSync().isNotEmpty()
    }
}
