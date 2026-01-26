package com.example.eloanmust.core.common

import com.example.eloanmust.BuildConfig

/**
 * Application-wide constants
 */
object Constants {
    
    // ============================================
    // API CONFIGURATION
    // ============================================
    const val BASE_URL = BuildConfig.BASE_URL
    const val API_TIMEOUT_SECONDS = 60L
    const val API_READ_TIMEOUT_SECONDS = 60L
    const val API_WRITE_TIMEOUT_SECONDS = 60L
    
    // ============================================
    // API ENDPOINTS
    // ============================================
    object Endpoints {
        // Auth
        const val LOGIN = "api/auth/login"
        const val REGISTER = "api/auth/register"
        const val LOGOUT = "api/auth/logout"
        const val FORGOT_PASSWORD = "api/auth/forgot-password"
        const val RESET_PASSWORD = "api/auth/reset-password"
        
        // Profile
        const val PROFILE = "api/profile"
        const val PROFILE_KTP = "api/profile/ktp"
        const val PROFILE_STATUS = "api/profile/status"
        
        // Plafond (Products)
        const val PLAFONDS = "api/plafonds"
        const val PLAFOND_DETECT = "api/plafonds/detect"
        
        // Loans
        const val LOANS = "api/loans"
        const val LOAN_SIMULATE = "api/loans/simulate"
        
        // Notifications
        const val NOTIFICATIONS = "api/notifications"
        const val NOTIFICATIONS_UNREAD = "api/notifications/unread"
        const val NOTIFICATIONS_COUNT = "api/notifications/count"
        const val NOTIFICATIONS_READ_ALL = "api/notifications/read-all"
    }
    
    // ============================================
    // DATABASE CONFIGURATION
    // ============================================
    const val DATABASE_NAME = "eloan_must_database"
    const val DATABASE_VERSION = 1
    
    // ============================================
    // DATASTORE KEYS
    // ============================================
    object DataStoreKeys {
        const val PREFERENCES_NAME = "eloan_preferences"
        const val ACCESS_TOKEN = "access_token"
        const val REFRESH_TOKEN = "refresh_token"
        const val USER_ID = "user_id"
        const val USERNAME = "username"
        const val USER_EMAIL = "user_email"
        const val USER_ROLE = "user_role"
        const val IS_LOGGED_IN = "is_logged_in"
        const val FCM_TOKEN = "fcm_token"
        const val IS_PROFILE_COMPLETE = "is_profile_complete"
        const val DARK_MODE = "dark_mode"
        const val FIRST_TIME_LAUNCH = "first_time_launch"
    }
    
    // ============================================
    // LOAN STATUS
    // ============================================
    object LoanStatus {
        const val PENDING_REVIEW = "PENDING_REVIEW"
        const val REVIEWED = "REVIEWED"
        const val APPROVED = "APPROVED"
        const val REJECTED = "REJECTED"
        const val DISBURSED = "DISBURSED"
    }
    
    // ============================================
    // NOTIFICATION TYPES
    // ============================================
    object NotificationType {
        const val LOAN_SUBMITTED = "LOAN_SUBMITTED"
        const val LOAN_REVIEWED = "LOAN_REVIEWED"
        const val LOAN_APPROVED = "LOAN_APPROVED"
        const val LOAN_REJECTED = "LOAN_REJECTED"
        const val LOAN_DISBURSED = "LOAN_DISBURSED"
        const val SYSTEM = "SYSTEM"
    }
    
    // ============================================
    // NAVIGATION ROUTES
    // ============================================
    object Routes {
        // Auth
        const val SPLASH = "splash"
        const val LANDING = "landing"
        const val LOGIN = "login"
        const val REGISTER = "register"
        const val FORGOT_PASSWORD = "forgot_password"
        const val RESET_PASSWORD = "reset_password/{token}"
        
        // Main
        const val HOME = "home"
        const val PRODUCTS = "products"
        const val PROFILE = "profile"
        const val NOTIFICATIONS = "notifications"
        
        // Loan
        const val LOAN_SIMULATION = "loan_simulation"
        const val LOAN_APPLY = "loan_apply"
        const val LOAN_HISTORY = "loan_history"
        const val LOAN_DETAIL = "loan_detail/{loanId}"
        
        // Profile
        const val EDIT_PROFILE = "edit_profile"
        const val UPLOAD_KTP = "upload_ktp"
        
        // Settings
        const val SETTINGS = "settings"
        const val ABOUT = "about"
    }
    
    // ============================================
    // VALIDATION
    // ============================================
    object Validation {
        const val MIN_PASSWORD_LENGTH = 8
        const val MAX_PASSWORD_LENGTH = 32
        const val MIN_USERNAME_LENGTH = 3
        const val MAX_USERNAME_LENGTH = 50
        const val NIK_LENGTH = 16
        const val PHONE_MIN_LENGTH = 10
        const val PHONE_MAX_LENGTH = 15
    }
    
    // ============================================
    // UI CONFIGURATION
    // ============================================
    object UI {
        const val ANIMATION_DURATION_SHORT = 150
        const val ANIMATION_DURATION_MEDIUM = 300
        const val ANIMATION_DURATION_LONG = 500
        const val SPLASH_DELAY = 2000L
        const val DEBOUNCE_DELAY = 300L
        const val SEARCH_DEBOUNCE_DELAY = 500L
    }
    
    // ============================================
    // PAGINATION
    // ============================================
    object Pagination {
        const val DEFAULT_PAGE_SIZE = 10
        const val INITIAL_PAGE = 0
    }
    
    // ============================================
    // CACHE CONFIGURATION
    // ============================================
    object Cache {
        const val CACHE_VALIDITY_HOURS = 1L
        const val MAX_CACHE_SIZE_MB = 50L
    }
}
