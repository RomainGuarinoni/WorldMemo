package com.example.worldmemo.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.example.worldmemo.MainActivity
import com.example.worldmemo.R
import com.example.worldmemo.broadcast.NotificationReceiver


class NotificationUtils(private val context: Context) {

    private val notificationChannelId = "uncreatedContentDelay"
    private val notificationChannelName = "uncreated content delay"
    private val notificationChannelDescription =
        "This channel id is dedicated to the notification" + "that attract the user to use the app if it is not used for a long time"
    private val notificationChannelImportance = NotificationManager.IMPORTANCE_DEFAULT
    private val notificationId = 1
    private val notificationTitle = "Add something new !"
    private val notificationMessage =
        "It's been a while that you didn't had something in the app, go find a new audio or image to add !!"


    val INTERVAL_MS = 30 * 1000 // 30 secondes

    fun createNotificationChannel() {
        val channel = NotificationChannel(
            notificationChannelId, notificationChannelName, notificationChannelImportance
        )
        channel.description = notificationChannelDescription

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun createNotification() {

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create an Intent for the activity you want to start
        val resultIntent = Intent(context, MainActivity::class.java)
        // Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(
                notificationId, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val notification = NotificationCompat.Builder(context, notificationChannelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle(notificationTitle)
            .setContentIntent(resultPendingIntent)
            .setContentText(notificationMessage).setChannelId(notificationChannelId).build()

        notification.flags = Notification.FLAG_AUTO_CANCEL

        manager.notify(notificationId, notification)
    }

    fun scheduleNotification() {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, notificationId, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val futureInMillis = SystemClock.elapsedRealtime() + INTERVAL_MS

        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent)
    }


}