package com.example.fomonator

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView

class NotificationAdapter(private val notifications: List<FomoNotificationWithUrgency>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]
        holder.appName.text = notification.notification.app.name
        holder.title.text = notification.notification.sender
        holder.content.text = notification.notification.msg
        if (notification.cancelled) {
            val concelledNotificatioColor = "#A0A0A0"
            holder.appName.setTextColor(Color.parseColor(concelledNotificatioColor))
            holder.title.setTextColor(Color.parseColor(concelledNotificatioColor))
            holder.content.setTextColor(Color.parseColor(concelledNotificatioColor))
        } else {
            val activeNotification = "#000000"
            holder.appName.setTextColor(Color.parseColor(activeNotification))
            holder.title.setTextColor(Color.parseColor(activeNotification))
            holder.content.setTextColor(Color.parseColor(activeNotification))
        }

        // Adjust the width of the urgency bar based on the urgency (0-10 scale)
        val urgencyPercentage: Double = (notification.urgency?:5) / 10.0
        //TODO co robic jak nie wiadomo jakie urgency - teraz default 5

        holder.urgencyBar.post {
            val maxWidth = holder.urgencyBar.parent as ViewGroup
            val newWidth = (maxWidth.width * urgencyPercentage).toInt()

            holder.urgencyBar.updateLayoutParams<ViewGroup.LayoutParams> {
                width = newWidth
            }
        }
    }

    override fun getItemCount() = notifications.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appName: TextView = view.findViewById(R.id.appName)
        val title: TextView = view.findViewById(R.id.title)
        val content: TextView = view.findViewById(R.id.content)
        val urgencyBar: View = view.findViewById(R.id.urgencyBar) // The urgency background bar
    }
}
