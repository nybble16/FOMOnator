package com.example.fomonator

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        // Extract notification details
        val notificationTitle = sbn.notification.extras.getString("android.title")
        val notificationText = sbn.notification.extras.getString("android.text")

        Log.d("NotificationListener", "Notification Posted: $notificationTitle - $notificationText")

        // Send broadcast to MainActivity
        val intent = Intent("com.example.fomonator.NOTIFICATION_LISTENER")
        intent.putExtra("notification_title", notificationTitle)
        intent.putExtra("notification_text", notificationText)
        sendBroadcast(intent)
        cancelNotification(sbn.key)
    }
}
