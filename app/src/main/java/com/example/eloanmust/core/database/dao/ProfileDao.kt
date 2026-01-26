package com.example.eloanmust.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eloanmust.feature.profile.data.local.ProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Profile entity.
 */
@Dao
interface ProfileDao {
    
    /**
     * Get profile by user ID
     */
    @Query("SELECT * FROM profiles WHERE userId = :userId LIMIT 1")
    fun getProfileByUserId(userId: Long): Flow<ProfileEntity?>
    
    /**
     * Get profile by user ID (suspend)
     */
    @Query("SELECT * FROM profiles WHERE userId = :userId LIMIT 1")
    suspend fun getProfileByUserIdSync(userId: Long): ProfileEntity?
    
    /**
     * Insert or update profile
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)
    
    /**
     * Delete profile by user ID
     */
    @Query("DELETE FROM profiles WHERE userId = :userId")
    suspend fun deleteProfile(userId: Long)
    
    /**
     * Clear all profiles
     */
    @Query("DELETE FROM profiles")
    suspend fun clearAll()
    
    /**
     * Update profile status
     */
    @Query("UPDATE profiles SET isProfileComplete = :isComplete, updatedAt = :updatedAt WHERE userId = :userId")
    suspend fun updateProfileStatus(userId: Long, isComplete: Boolean, updatedAt: Long)
}
