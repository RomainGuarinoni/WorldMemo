package com.example.worldmemo.broadcast


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.worldmemo.utils.NotificationUtils

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationUtils(context).createNotification(intent)
    }

}