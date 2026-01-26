package com.example.eloanmust

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application class for E-Loan Must.
 * Initializes Hilt, Firebase, and Timber logging.
 */
@HiltAndroidApp
class EloanMustApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        Timber.d("E-Loan Must Application initialized")
    }
}
