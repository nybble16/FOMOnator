package com.example.fomonator

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import java.time.Duration
import java.time.Instant

class NotificationAdapter(private val notifications: List<FomoNotificationWithUrgency>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]
        holder.title.text = notification.notification.sender
        holder.content.text = notification.notification.msg
        holder.avatar.setImageResource(R.drawable.messenger)

        if (notification.cancelled) {
            holder.card.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
        } else {
            holder.card.setCardBackgroundColor(Color.parseColor("#E2CFFA"))
        }

        val timeAgo = getTimeDifference(notification.notification.postTime)
        holder.timeAgo.text = " · $timeAgo"
        holder.urgency.text = "${(notification.urgency?:0) * 10}%"
    }

    override fun getItemCount() = notifications.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.avatar)
        val timeAgo: TextView = view.findViewById(R.id.time)
        val title: TextView = view.findViewById(R.id.sender)
        val urgency: TextView = view.findViewById(R.id.urgency)
        val content: TextView = view.findViewById(R.id.content)
        val card: CardView = view.findViewById(R.id.card)
    }

    fun formatDuration(duration: Duration): String {
        val seconds = duration.seconds

        val days = seconds / (24 * 3600)
        val hours = (seconds % (24 * 3600)) / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        return when {
            days > 0 -> "${days}d"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}m"
            secs > 0 -> "${secs}s"
            else -> "0s"
        }
    }

    fun getTimeDifference(epochMillis: Long): String {
        // Get the current time in epoch millis
        val nowMillis = Instant.now().toEpochMilli()

        // Calculate the difference between now and the given epoch time
        val diffMillis = nowMillis - epochMillis

        // Convert the difference to a Duration object
        val duration = Duration.ofMillis(diffMillis)

        // Format and return the duration
        return formatDuration(duration)
    }
}
