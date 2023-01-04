package com.example.worldmemo.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.worldmemo.R

class NotificationUtils(private val context: Context) {

    private val notificationChannelId = "uncreatedContentDelay"
    private val notificationChannelName = "uncreated content delay"
    private val notificationChannelDescription =
        "This channel id is dedicated to the notification" + "that attract the user to use the app if it is not used for a long time"
    private val notificationChannelImportance = NotificationManager.IMPORTANCE_DEFAULT

    private val title = "title"
    private val message = "message"

    fun createNotificationChannel() {
        val channel = NotificationChannel(
            notificationChannelId, notificationChannelName, notificationChannelImportance
        )
        channel.description = notificationChannelDescription

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun createNotification(intent: Intent) {
        val notification = NotificationCompat.Builder(context, notificationChannelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent.getStringExtra(title))
            .setContentText(intent.getStringExtra(message)).setChannelId(notificationChannelId)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification)
    }

}