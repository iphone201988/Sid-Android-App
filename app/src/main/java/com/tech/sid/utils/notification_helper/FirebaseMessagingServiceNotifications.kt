package com.tech.sid.utils.notification_helper

import com.tech.sid.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.tech.sid.ui.dashboard.dashboard_with_fragment.DashboardActivity
import org.json.JSONObject

class FirebaseMessagingServiceNotifications : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            checkNotification(remoteMessage.data)
            if (isLongRunningJob()) {
                scheduleJob()
            } else {
                handleNow()
            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            Log.d(TAG, "Message Notification Body: ${remoteMessage.data}")
        }

    }

    private fun isLongRunningJob() = true

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private fun scheduleJob() {
    }

    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    private fun sendRegistrationToServer(token: String?) {
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    private fun checkNotification(messageBody: MutableMap<String, String>) {
        try {
            val gson = Gson()
            val obj =
                JSONObject(messageBody as Map<String, Any>) // Explicit cast to Map<String, Any>
            val data: NotificationPayload =
                gson.fromJson(obj.toString(), NotificationPayload::class.java)
            sendNotification(data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendNotification(data: NotificationPayload) {
        val requestCode = 0
        val intent =   Intent(this, DashboardActivity::class.java).apply {}
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = getString(R.string.channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder =
            NotificationCompat.Builder(this, channelId).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(data.title).setContentText(data.body).setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH).setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    companion object {
        val TAG = "Firebase Notifications"
    }
}