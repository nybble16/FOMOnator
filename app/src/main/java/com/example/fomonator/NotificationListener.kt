package com.example.fomonator

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.google.gson.Gson
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.*

class NotificationListener : NotificationListenerService() {

    val llmClassifier: LLMClassifier = LLamaClassifier()
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        // Extract notification details
        val notificationTitle = sbn.notification.extras.getString("android.conversationTitle")
        val notificationText = sbn.notification.extras.getString("android.text")
        val fomoNotification = FomoNotification(mapToApp(sbn.packageName), notificationTitle, notificationText, sbn.postTime)

        Log.d("NotificationListener", "Notification Posted: $fomoNotification")
        val allRelatedNotifications = NotificationRepository.insertNotification(fomoNotification)
        Log.d("NotificationListener", "Found ${allRelatedNotifications.size} related notifications")

        serviceScope.launch {
            //TODO change to list
//        val urgency = llmClassifier.urgencify(allRelatedNotifications)
            val urgency = llmClassifier.urgencify(fomoNotification)

            val fomoNotificationWithUrgency = FomoNotificationWithUrgency(fomoNotification, urgency)

            if (urgency != null && urgency <= 5) {
                cancelNotification(sbn.key)
                Log.d("NotificationListener", "Cancelling Notification Key: ${sbn.key}")
            } else Log.d(
                "NotificationListener",
                "Passing Notification: ${fomoNotificationWithUrgency}"
            )

            val intent = Intent("com.example.fomonator.NOTIFICATION_LISTENER")
            intent.putExtra("notification_title", notificationTitle)
            intent.putExtra("notification_text", notificationText)
            intent.putExtra("fomoNotification", Gson().toJson(fomoNotificationWithUrgency))
            sendBroadcast(intent)
        }
    }
}

fun mapToApp(packageName: String): FomoApp = when (packageName) {
    "com.facebook.orca" -> FomoApp.MESSENGER
    else -> FomoApp.OTHER
}

enum class FomoApp { MESSENGER, OTHER }
data class FomoNotification(val app: FomoApp, val sender: String?, val msg: String?, val postTime: Long)
data class FomoNotificationWithUrgency(val notification: FomoNotification, val urgency: Int?)

data class FomoGrouping(val app: FomoApp, val sender: String)
object NotificationRepository {
    val notifications = mutableListOf<FomoNotification>()

    fun insertNotification(fomoNotification: FomoNotification): List<FomoNotification> {
        notifications.add(fomoNotification)

        return notifications.groupBy { it.app to it.sender }
            .get(fomoNotification.app to fomoNotification.sender)
            ?: emptyList()
    }

}