package com.example.eloanmust.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.eloanmust.core.common.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// DataStore extension property
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = Constants.DataStoreKeys.PREFERENCES_NAME
)

/**
 * Token Manager for handling JWT tokens and user session data.
 * Uses DataStore for persistent storage.
 */
@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore
    
    // Keys
    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey(Constants.DataStoreKeys.ACCESS_TOKEN)
        val REFRESH_TOKEN = stringPreferencesKey(Constants.DataStoreKeys.REFRESH_TOKEN)
        val USER_ID = longPreferencesKey(Constants.DataStoreKeys.USER_ID)
        val USERNAME = stringPreferencesKey(Constants.DataStoreKeys.USERNAME)
        val USER_EMAIL = stringPreferencesKey(Constants.DataStoreKeys.USER_EMAIL)
        val USER_ROLE = stringPreferencesKey(Constants.DataStoreKeys.USER_ROLE)
        val IS_LOGGED_IN = booleanPreferencesKey(Constants.DataStoreKeys.IS_LOGGED_IN)
        val FCM_TOKEN = stringPreferencesKey(Constants.DataStoreKeys.FCM_TOKEN)
        val IS_PROFILE_COMPLETE = booleanPreferencesKey(Constants.DataStoreKeys.IS_PROFILE_COMPLETE)
        val DARK_MODE = booleanPreferencesKey(Constants.DataStoreKeys.DARK_MODE)
        val FIRST_TIME_LAUNCH = booleanPreferencesKey(Constants.DataStoreKeys.FIRST_TIME_LAUNCH)
    }
    
    // ============================================
    // TOKEN FLOWS
    // ============================================
    
    val accessToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[Keys.ACCESS_TOKEN]
    }
    
    val refreshToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[Keys.REFRESH_TOKEN]
    }
    
    val isLoggedIn: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.IS_LOGGED_IN] == true
    }
    
    val fcmToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[Keys.FCM_TOKEN]
    }
    
    // ============================================
    // USER DATA FLOWS
    // ============================================
    
    val userId: Flow<Long?> = dataStore.data.map { preferences ->
        preferences[Keys.USER_ID]
    }
    
    val username: Flow<String?> = dataStore.data.map { preferences ->
        preferences[Keys.USERNAME]
    }
    
    val userEmail: Flow<String?> = dataStore.data.map { preferences ->
        preferences[Keys.USER_EMAIL]
    }
    
    val userRole: Flow<String?> = dataStore.data.map { preferences ->
        preferences[Keys.USER_ROLE]
    }
    
    val isProfileComplete: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.IS_PROFILE_COMPLETE] == true
    }
    
    // ============================================
    // SETTINGS FLOWS
    // ============================================
    
    val darkMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.DARK_MODE] == true
    }
    
    val isFirstTimeLaunch: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.FIRST_TIME_LAUNCH] != false
    }
    
    // ============================================
    // SAVE FUNCTIONS
    // ============================================
    
    /**
     * Save login data (tokens and user info)
     */
    suspend fun saveLoginData(
        accessToken: String,
        refreshToken: String? = null,
        userId: Long,
        username: String,
        email: String,
        role: String
    ) {
        dataStore.edit { preferences ->
            preferences[Keys.ACCESS_TOKEN] = accessToken
            refreshToken?.let { preferences[Keys.REFRESH_TOKEN] = it }
            preferences[Keys.USER_ID] = userId
            preferences[Keys.USERNAME] = username
            preferences[Keys.USER_EMAIL] = email
            preferences[Keys.USER_ROLE] = role
            preferences[Keys.IS_LOGGED_IN] = true
        }
    }
    
    /**
     * Save tokens only
     */
    suspend fun saveTokens(accessToken: String, refreshToken: String? = null) {
        dataStore.edit { preferences ->
            preferences[Keys.ACCESS_TOKEN] = accessToken
            refreshToken?.let { preferences[Keys.REFRESH_TOKEN] = it }
        }
    }
    
    /**
     * Save FCM token
     */
    suspend fun saveFcmToken(token: String) {
        dataStore.edit { preferences ->
            preferences[Keys.FCM_TOKEN] = token
        }
    }
    
    /**
     * Update profile completion status
     */
    suspend fun saveProfileComplete(isComplete: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.IS_PROFILE_COMPLETE] = isComplete
        }
    }
    
    /**
     * Update dark mode preference
     */
    suspend fun saveDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.DARK_MODE] = enabled
        }
    }
    
    /**
     * Mark first time launch as completed
     */
    suspend fun setFirstTimeLaunchComplete() {
        dataStore.edit { preferences ->
            preferences[Keys.FIRST_TIME_LAUNCH] = false
        }
    }
    
    // ============================================
    // CLEAR FUNCTIONS
    // ============================================
    
    /**
     * Clear all login-related data (logout)
     * Keeps FCM token and app settings
     */
    suspend fun clearLoginData() {
        dataStore.edit { preferences ->
            preferences.remove(Keys.ACCESS_TOKEN)
            preferences.remove(Keys.REFRESH_TOKEN)
            preferences.remove(Keys.USER_ID)
            preferences.remove(Keys.USERNAME)
            preferences.remove(Keys.USER_EMAIL)
            preferences.remove(Keys.USER_ROLE)
            preferences.remove(Keys.IS_PROFILE_COMPLETE)
            preferences[Keys.IS_LOGGED_IN] = false
        }
    }
    
    /**
     * Clear all data (full reset)
     */
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
