package com.example.eloanmust.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eloanmust.feature.product.data.local.PlafondEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Plafond (Loan Product) entity.
 */
@Dao
interface PlafondDao {

    /**
     * Get all active plafonds
     */
    @Query("SELECT * FROM plafonds WHERE isActive = 1 ORDER BY minAmount ASC")
    fun getActivePlafonds(): Flow<List<PlafondEntity>>

    /**
     * Get all plafonds (sync)
     */
    @Query("SELECT * FROM plafonds WHERE isActive = 1 ORDER BY minAmount ASC")
    suspend fun getActivePlafondsSync(): List<PlafondEntity>

    /**
     * Get plafond by ID
     */
    @Query("SELECT * FROM plafonds WHERE id = :plafondId LIMIT 1")
    suspend fun getPlafondById(plafondId: Long): PlafondEntity?

    /**
     * Get plafond by ID (Flow)
     */
    @Query("SELECT * FROM plafonds WHERE id = :plafondId LIMIT 1")
    fun getPlafondByIdFlow(plafondId: Long): Flow<PlafondEntity?>

    /**
     * Find plafond for given amount
     */
    @Query("SELECT * FROM plafonds WHERE isActive = 1 AND minAmount <= :amount AND maxAmount >= :amount LIMIT 1")
    suspend fun findPlafondForAmount(amount: Double): PlafondEntity?

    /**
     * Insert or update plafond
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlafond(plafond: PlafondEntity)

    /**
     * Insert multiple plafonds
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlafonds(plafonds: List<PlafondEntity>)

    /**
     * Delete plafond by ID
     */
    @Query("DELETE FROM plafonds WHERE id = :plafondId")
    suspend fun deletePlafond(plafondId: Long)

    /**
     * Clear all plafonds
     */
    @Query("DELETE FROM plafonds")
    suspend fun clearAll()

    /**
     * Get cache timestamp
     */
    @Query("SELECT MAX(cachedAt) FROM plafonds")
    suspend fun getLastCacheTime(): Long?
}
