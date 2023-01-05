package com.example.worldmemo.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.worldmemo.R
import com.example.worldmemo.broadcast.NotificationReceiver


class NotificationUtils(private val context: Context) {

    private val notificationChannelId = "uncreatedContentDelay"
    private val notificationChannelName = "uncreated content delay"
    private val notificationChannelDescription =
        "This channel id is dedicated to the notification" + "that attract the user to use the app if it is not used for a long time"
    private val notificationChannelImportance = NotificationManager.IMPORTANCE_DEFAULT

    private val title = "title"
    private val message = "message"

    val INTERVAL_MS = 60000L

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

    fun scheduleNotification() {
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra(title, "Add something new !")
        intent.putExtra(message, "It's been a while that you didn't had something in the app, go find a new audio or image to add !!")
        val pendingIntent = PendingIntent.getBroadcast(
            context, 1, intent, PendingIntent.FLAG_MUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent);


        alarmManager.set(
            AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL_MS, pendingIntent
        );
    }


}