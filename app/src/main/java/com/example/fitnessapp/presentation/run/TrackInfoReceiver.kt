package com.example.fitnessapp.presentation.run

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import com.example.fitnessapp.data.model.point.PointDto
import java.util.ArrayList

class TrackInfoReceiver : BroadcastReceiver() {

    companion object {
        private const val DEFAULT_DISTANCE_VALUE = 0
    }

    private var onTrackInfoReceivedListener: OnTrackInfoReceivedListener? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == LocationService.BROADCAST_ACTION_SEND_TRACK_INFO) {
            val locations = intent.getParcelableArrayListExtra<Location>(LocationService.ALL_POINTS)
            val distance = intent.getIntExtra(
                LocationService.DISTANCE,
                DEFAULT_DISTANCE_VALUE
            )
            val points = locations?.let { convertLocationsToPointDto(it) } ?: emptyList()
            onTrackInfoReceivedListener?.onTrackInfoReceived(points, distance)
        }
    }

    private fun convertLocationsToPointDto(locations: ArrayList<Location>): List<PointDto> {
        val convertedPoints = mutableListOf<PointDto>()
        for (location in locations) {
            convertedPoints.add(PointDto(location.longitude, location.latitude))
        }
        return convertedPoints
    }

    fun setOnTrackInfoReceivedListener(onTrackInfoReceivedListener: OnTrackInfoReceivedListener) {
        this.onTrackInfoReceivedListener = onTrackInfoReceivedListener
    }

    interface OnTrackInfoReceivedListener {
        fun onTrackInfoReceived(receivedPoints: List<PointDto>, distance: Int)
    }
}