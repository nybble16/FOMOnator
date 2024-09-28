package com.example.fomonator

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.google.gson.Gson
import kotlin.random.Random

class NotificationListener : NotificationListenerService() {

    val llmClassifier = MockLLMClassifier()

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        // Extract notification details
        val notificationTitle = sbn.notification.extras.getString("android.conversationTitle")
        val notificationText = sbn.notification.extras.getString("android.text")
        val fomoNotification = FomoNotification(mapToApp(sbn.packageName), notificationTitle, notificationText, sbn.postTime)

        Log.d("NotificationListener", "Notification Posted: $fomoNotification")
        val urgency = llmClassifier.urgencify(fomoNotification)
        val fomoNotificationWithUrgency = FomoNotificationWithUrgency(fomoNotification, urgency)
        Log.d("NotificationListener", "Notification Classified: $fomoNotificationWithUrgency")

        if (urgency > 5)
            cancelNotification(sbn.key)

        val intent = Intent("com.example.fomonator.NOTIFICATION_LISTENER")
        intent.putExtra("notification_title", notificationTitle)
        intent.putExtra("notification_text", notificationText)
        intent.putExtra("fomoNotification", Gson().toJson(fomoNotificationWithUrgency))
        sendBroadcast(intent)

    }
}

fun mapToApp(packageName: String): FomoApp = when (packageName) {
    "com.facebook.orca" -> FomoApp.MESSENGER
    else -> FomoApp.OTHER
}

enum class FomoApp { MESSENGER, OTHER }
data class FomoNotification(val app: FomoApp, val sender: String?, val msg: String?, val postTime: Long)
data class FomoNotificationWithUrgency(val notification: FomoNotification, val urgency: Int, )

//class NotificationRepository {
//    fun insertNotification(fomoNotification: FomoNotification) {}
//    fun
//
//}