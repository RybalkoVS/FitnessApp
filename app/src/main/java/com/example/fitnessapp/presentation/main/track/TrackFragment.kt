package com.example.fitnessapp.presentation.main.track

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import bolts.Task
import com.example.fitnessapp.FitnessApp
import com.example.fitnessapp.R
import com.example.fitnessapp.data.model.point.PointDbo
import com.example.fitnessapp.data.model.track.TrackDbo
import com.example.fitnessapp.toLatLng
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*

class TrackFragment : Fragment(R.layout.fragment_track), OnMapReadyCallback {

    companion object {
        const val TAG = "TRACK_FRAGMENT"
        const val TRACK_ITEM_EXTRA = "TRACK_ITEM_EXTRA"
        private const val TRACK_COLOR = Color.BLUE
        private const val TRACK_WIDTH = 10f
        private const val START_MARKER_COLOR = BitmapDescriptorFactory.HUE_GREEN
        private const val FINISH_MARKER_COLOR = BitmapDescriptorFactory.HUE_RED
        private const val MAP_PADDING = 100

        fun newInstance(args: Bundle?) = TrackFragment().apply {
            arguments = args
        }
    }

    private val localRepository = FitnessApp.INSTANCE.localRepository
    private val toastProvider = FitnessApp.INSTANCE.toastProvider
    private val points = mutableListOf<PointDbo>()
    private var track: TrackDbo? = null
    private lateinit var mapView: MapView
    private lateinit var distanceTextView: TextView
    private lateinit var durationTextView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        initMap(savedInstanceState)
        arguments?.let {
            track = it.getParcelable(TRACK_ITEM_EXTRA)
            distanceTextView.text = track?.distance.toString()
            durationTextView.text = track?.durationInMinutes
        }
    }

    private fun initViews(v: View) {
        distanceTextView = v.findViewById(R.id.text_distance_value)
        durationTextView = v.findViewById(R.id.text_duration_value)
        mapView = v.findViewById(R.id.map)
    }

    private fun initMap(savedInstanceState: Bundle?) {
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onMapReady(map: GoogleMap) {
        getTrackPoints()?.onSuccess({
            val polylinePoints = mutableListOf<LatLng>()
            for (point in points) {
                polylinePoints.add(point.toLatLng())
            }
            drawTrack(map, polylinePoints)
            map.addMarker(getStartMarkerOptions(polylinePoints.first()))
            map.addMarker(getFinishMarkerOptions(polylinePoints.last()))
            val midPoint = polylinePoints[polylinePoints.size / 2]
            map.animateCamera(
                moveCameraOnTrack(
                    listOf(
                        polylinePoints.first(),
                        polylinePoints.last(),
                        midPoint
                    )
                )
            )
        }, Task.UI_THREAD_EXECUTOR)
    }

    private fun getTrackPoints(): Task<Any>? {
        return track?.let {
            localRepository.getTrackPoints(it.id).continueWith { task ->
                if (task.error != null) {
                    toastProvider.showErrorMessage(error = task.error.message.toString())
                } else {
                    points.clear()
                    points.addAll(task.result)
                }
            }
        }
    }

    private fun drawTrack(map: GoogleMap, polylinePoints: List<LatLng>) {
        map.addPolyline(
            PolylineOptions().addAll(polylinePoints)
                .color(TRACK_COLOR)
                .width(TRACK_WIDTH)
        )
    }

    private fun getStartMarkerOptions(position: LatLng): MarkerOptions {
        return MarkerOptions().position(position)
            .title(getString(R.string.start_marker))
            .icon(BitmapDescriptorFactory.defaultMarker(START_MARKER_COLOR))
    }

    private fun getFinishMarkerOptions(position: LatLng): MarkerOptions {
        return MarkerOptions().position(position)
            .title(getString(R.string.finish_marker))
            .icon(BitmapDescriptorFactory.defaultMarker(FINISH_MARKER_COLOR))
    }

    private fun moveCameraOnTrack(pointList: List<LatLng>): CameraUpdate {
        val boundsBuilder = LatLngBounds.builder()
        for (point in pointList) {
            boundsBuilder.include(point)
        }
        val latLngBounds = boundsBuilder.build()
        return CameraUpdateFactory.newLatLngBounds(latLngBounds, MAP_PADDING)
    }

}