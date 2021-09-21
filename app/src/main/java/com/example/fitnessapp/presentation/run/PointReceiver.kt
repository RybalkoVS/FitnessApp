package com.example.fitnessapp.presentation.run

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.fitnessapp.data.model.point.PointDto

class PointReceiver : BroadcastReceiver() {

    companion object {
        private const val DEFAULT_DOUBLE_VALUE = 0.0
    }

    private var onPointReceivedListener: OnPointReceivedListener? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == LocationService.BROADCAST_ACTION_SEND_POINT) {
            val latitude = intent.getDoubleExtra(
                LocationService.LATITUDE,
                DEFAULT_DOUBLE_VALUE
            )
            val longitude = intent.getDoubleExtra(
                LocationService.LONGITUDE,
                DEFAULT_DOUBLE_VALUE
            )
            val point = PointDto(longitude = longitude, latitude = latitude)
            onPointReceivedListener?.onPointReceived(point)
        }
    }

    fun setOnPointReceivedListener(onPointReceivedListener: OnPointReceivedListener) {
        this.onPointReceivedListener = onPointReceivedListener
    }

    interface OnPointReceivedListener {
        fun onPointReceived(point: PointDto)
    }
}