package com.example.fomonator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val notificationList = mutableListOf<FomoNotificationWithUrgency>()
    private lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Tag", "Main activity started log")
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        adapter = NotificationAdapter(notificationList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Open notification settings
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        startActivity(intent)

        // Register BroadcastReceiver to receive notifications from NotificationListenerService
        val filter = IntentFilter("com.example.fomonator.NOTIFICATION_LISTENER")
        registerReceiver(notificationReceiver, filter, RECEIVER_EXPORTED)
    }

    private val notificationReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val allData = intent?.getStringExtra("fomoNotification")

            if (allData != null) {
                val notificationDetails = "$allData"
                notificationList.add(0, Gson().fromJson(allData, FomoNotificationWithUrgency::class.java))
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationReceiver)
    }
}
