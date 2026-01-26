package com.example.eloanmust.core.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.eloanmust.MainActivity
import com.example.eloanmust.R
import com.example.eloanmust.core.common.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Firebase Cloud Messaging Service for handling push notifications.
 * Receives and processes FCM messages, handles token refresh, and displays notifications.
 */
@AndroidEntryPoint
class EloanFirebaseMessagingService : FirebaseMessagingService() {
    
    @Inject
    lateinit var fcmTokenManager: FcmTokenManager
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    companion object {
        private const val CHANNEL_ID = "eloan_notifications"
        private const val CHANNEL_NAME = "E-Loan Notifications"
        private const val CHANNEL_DESCRIPTION = "Notifications for loan status updates"
        
        // Notification data keys
        private const val KEY_LOAN_ID = "loanId"
        private const val KEY_NOTIFICATION_TYPE = "notificationType"
        private const val KEY_STATUS = "status"
        private const val KEY_TITLE = "title"
        private const val KEY_BODY = "body"
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    /**
     * Called when a new FCM token is generated.
     * This can happen when:
     * - App is installed fresh
     * - App data is cleared
     * - App is restored on a new device
     * - Firebase SDK internally generates a new token
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("New FCM token received: $token")
        
        serviceScope.launch {
            fcmTokenManager.updateToken(token)
            // Note: If user is logged in, token should be sent to backend
            // This is handled by the auth flow when user logs in
        }
    }
    
    /**
     * Called when a message is received.
     * This is called for both:
     * - Data messages (always delivered to this callback)
     * - Notification messages when app is in foreground
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Timber.d("FCM Message received from: ${remoteMessage.from}")
        
        // Check if message contains data payload
        if (remoteMessage.data.isNotEmpty()) {
            Timber.d("Message data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }
        
        // Check if message contains notification payload
        remoteMessage.notification?.let { notification ->
            Timber.d("Message notification: ${notification.title} - ${notification.body}")
            showNotification(
                title = notification.title ?: "E-Loan Must",
                body = notification.body ?: "",
                data = remoteMessage.data
            )
        }
    }
    
    /**
     * Handle data message payload
     */
    private fun handleDataMessage(data: Map<String, String>) {
        val notificationType = data[KEY_NOTIFICATION_TYPE]
        val loanId = data[KEY_LOAN_ID]
        val status = data[KEY_STATUS]
        val title = data[KEY_TITLE]
        val body = data[KEY_BODY]
        
        Timber.d("Notification type: $notificationType, Loan ID: $loanId, Status: $status")
        
        // Show notification if data contains title and body
        if (!title.isNullOrBlank() && !body.isNullOrBlank()) {
            showNotification(
                title = title,
                body = body,
                data = data
            )
        }
        
        // Handle specific notification types
        when (notificationType) {
            Constants.NotificationType.LOAN_SUBMITTED -> {
                Timber.d("Loan submitted notification for loan: $loanId")
            }
            Constants.NotificationType.LOAN_REVIEWED -> {
                Timber.d("Loan reviewed notification for loan: $loanId")
            }
            Constants.NotificationType.LOAN_APPROVED -> {
                Timber.d("Loan approved notification for loan: $loanId")
            }
            Constants.NotificationType.LOAN_REJECTED -> {
                Timber.d("Loan rejected notification for loan: $loanId")
            }
            Constants.NotificationType.LOAN_DISBURSED -> {
                Timber.d("Loan disbursed notification for loan: $loanId")
            }
        }
    }
    
    /**
     * Display notification to user
     */
    private fun showNotification(
        title: String,
        body: String,
        data: Map<String, String>
    ) {
        val intent = createNotificationIntent(data)
        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(body)
            )
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Use unique ID for each notification
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
        
        Timber.d("Notification displayed with ID: $notificationId")
    }
    
    /**
     * Create intent for notification click action
     * Handles deep linking to specific screens
     */
    private fun createNotificationIntent(data: Map<String, String>): Intent {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        
        // Add data extras for deep linking
        val loanId = data[KEY_LOAN_ID]
        val notificationType = data[KEY_NOTIFICATION_TYPE]
        
        if (!loanId.isNullOrBlank()) {
            intent.putExtra(KEY_LOAN_ID, loanId)
        }
        if (!notificationType.isNullOrBlank()) {
            intent.putExtra(KEY_NOTIFICATION_TYPE, notificationType)
        }
        
        // Set deep link route
        val route = when (notificationType) {
            Constants.NotificationType.LOAN_SUBMITTED,
            Constants.NotificationType.LOAN_REVIEWED,
            Constants.NotificationType.LOAN_APPROVED,
            Constants.NotificationType.LOAN_REJECTED,
            Constants.NotificationType.LOAN_DISBURSED -> {
                if (!loanId.isNullOrBlank()) {
                    "loan_detail/$loanId"
                } else {
                    Constants.Routes.LOAN_HISTORY
                }
            }
            else -> Constants.Routes.NOTIFICATIONS
        }
        
        intent.putExtra("deeplink_route", route)
        
        return intent
    }
    
    /**
     * Create notification channel for Android O and above
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            
            Timber.d("Notification channel created: $CHANNEL_ID")
        }
    }
}
