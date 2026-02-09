package com.example.eloanmust

import android.app.Application
import androidx.work.Configuration
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * Application class for E-Loan Must.
 * Initializes Hilt, Firebase, and Timber logging.
 */
@HiltAndroidApp
class EloanMustApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: androidx.hilt.work.HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        Timber.d("E-Loan Must Application initialized")

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelId = com.example.eloanmust.core.common.Constants.NotificationChannel.CHANNEL_ID
            val channelName = com.example.eloanmust.core.common.Constants.NotificationChannel.CHANNEL_NAME
            val channelDescription = com.example.eloanmust.core.common.Constants.NotificationChannel.CHANNEL_DESCRIPTION

            val channel = android.app.NotificationChannel(
                channelId,
                channelName,
                android.app.NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = channelDescription
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager = getSystemService(android.app.NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            Timber.d("Notification channel created: $channelId")
        }
    }
}
