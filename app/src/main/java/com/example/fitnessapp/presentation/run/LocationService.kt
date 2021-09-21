package com.example.fitnessapp.presentation.run

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat


class LocationService : Service(), LocationListener {

    companion object {
        private const val TIME_BETWEEN_UPDATE = 3000L
        private const val UPDATE_DISTANCE_IN_METRES = 5.0f
        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_TITLE = "Fitness Tracker"
        private const val NOTIFICATION_MESSAGE = "Tracking..."
        private const val NOTIFICATION_CHANNEL_ID = "FitnessApp"
        private const val NOTIFICATION_CHANNEL_NAME = "FitnessAppChannel"
        private const val NOTIFICATION_CHANNEL_DESCRIPTION = "Channel for location service"
        const val BROADCAST_ACTION_SEND_POINT = "SEND_POINT"
        const val LATITUDE = "LATITUDE"
        const val LONGITUDE = "LONGITUDE"
    }

    private lateinit var locationManager: LocationManager
    private var isActive = false
    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isActive) {
            isActive = true
            requestLocationUpdates()
        }

        createNotificationChannel(this)

        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, RunActivity::class.java),
                    0
                )
            )
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText(NOTIFICATION_MESSAGE)
            .setAutoCancel(false)
            .setOngoing(true)

        startForeground(NOTIFICATION_ID, builder.build())
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            TIME_BETWEEN_UPDATE,
            UPDATE_DISTANCE_IN_METRES,
            this
        )
    }

    override fun onLocationChanged(location: Location) {
        val intent = configureIntent(location.latitude, location.longitude)
        sendBroadcast(intent)
    }

    private fun configureIntent(latitude: Double, longitude: Double): Intent {
        return Intent(BROADCAST_ACTION_SEND_POINT).apply {
            putExtra(LATITUDE, latitude)
            putExtra(LONGITUDE, longitude)
        }
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

    override fun onDestroy() {
        if (isActive) {
            isActive = false
            locationManager.removeUpdates(this)
            stopForeground(true)
        }
        super.onDestroy()
    }
}