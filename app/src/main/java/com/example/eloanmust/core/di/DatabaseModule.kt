package com.example.eloanmust.core.di

import android.content.Context
import androidx.room.Room
import com.example.eloanmust.core.database.AppDatabase
import com.example.eloanmust.core.database.dao.LoanDao
import com.example.eloanmust.core.database.dao.NotificationDao
import com.example.eloanmust.core.database.dao.PlafondDao
import com.example.eloanmust.core.database.dao.ProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for database-related dependencies.
 * Provides Room Database and DAO instances.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Provides Room Database instance
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    /**
     * Provides ProfileDao instance
     */
    @Provides
    @Singleton
    fun provideProfileDao(database: AppDatabase): ProfileDao {
        return database.profileDao()
    }
    
    /**
     * Provides LoanDao instance
     */
    @Provides
    @Singleton
    fun provideLoanDao(database: AppDatabase): LoanDao {
        return database.loanDao()
    }
    
    /**
     * Provides NotificationDao instance
     */
    @Provides
    @Singleton
    fun provideNotificationDao(database: AppDatabase): NotificationDao {
        return database.notificationDao()
    }
    
    /**
     * Provides PlafondDao instance
     */
    @Provides
    @Singleton
    fun providePlafondDao(database: AppDatabase): PlafondDao {
        return database.plafondDao()
    }
}
