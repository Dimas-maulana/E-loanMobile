package com.example.eloanmust.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.eloanmust.core.database.dao.LoanDao
import com.example.eloanmust.core.database.dao.NotificationDao
import com.example.eloanmust.core.database.dao.PlafondDao
import com.example.eloanmust.core.database.dao.ProfileDao
import com.example.eloanmust.feature.loan.data.local.LoanEntity
import com.example.eloanmust.feature.notification.data.local.NotificationEntity
import com.example.eloanmust.feature.product.data.local.PlafondEntity
import com.example.eloanmust.feature.profile.data.local.ProfileEntity

/**
 * Main Room Database for E-Loan Must application.
 * Contains all local entities for offline-first support.
 */
@Database(
    entities = [
        ProfileEntity::class,
        LoanEntity::class,
        NotificationEntity::class,
        PlafondEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Profile DAO for customer profile operations
     */
    abstract fun profileDao(): ProfileDao
    
    /**
     * Loan DAO for loan history operations
     */
    abstract fun loanDao(): LoanDao
    
    /**
     * Notification DAO for notification operations
     */
    abstract fun notificationDao(): NotificationDao
    
    /**
     * Plafond DAO for loan product caching
     */
    abstract fun plafondDao(): PlafondDao
    
    companion object {
        const val DATABASE_NAME = "eloan_must_database"
    }
}
