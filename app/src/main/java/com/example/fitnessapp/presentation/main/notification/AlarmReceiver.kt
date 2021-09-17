package com.example.fitnessapp.presentation.main.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fitnessapp.R
import com.example.fitnessapp.presentation.run.RunActivity


class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val NOTIFICATION_TITLE = "Fitness Tracker"
        private const val NOTIFICATION_MESSAGE = "It's time to run!"
        private const val NOTIFICATION_CHANNEL_ID = "FitnessApp"
        private const val NOTIFICATION_CHANNEL_NAME = "FitnessAppChannel"
        private const val NOTIFICATION_CHANNEL_DESCRIPTION = "Channel for alarm manager"
    }

    override fun onReceive(context: Context, intent: Intent) {
        createNotificationChannel(context)

        val runActivityIntent = Intent(context, RunActivity::class.java)

        val contentIntent = PendingIntent.getActivity(context, 0, runActivityIntent, 0)

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText(NOTIFICATION_MESSAGE)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentIntent)
            .build()

        NotificationManagerCompat.from(context).notify(0, notification)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = NOTIFICATION_CHANNEL_DESCRIPTION
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }
}