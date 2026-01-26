package com.example.eloanmust.core.database

import androidx.room.TypeConverter
import java.util.Date

/**
 * Type converters for Room database.
 * Converts complex types to primitives and vice versa.
 */
class Converters {
    
    /**
     * Convert Long timestamp to Date
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    /**
     * Convert Date to Long timestamp
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    /**
     * Convert String list to comma-separated string
     */
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return list?.joinToString(",")
    }
    
    /**
     * Convert comma-separated string to String list
     */
    @TypeConverter
    fun toStringList(data: String?): List<String>? {
        return data?.split(",")?.filter { it.isNotBlank() }
    }
}
