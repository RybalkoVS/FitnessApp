package com.example.fitnessapp.presentation.main.track

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bolts.Task
import com.example.fitnessapp.FitnessApp
import com.example.fitnessapp.R
import com.example.fitnessapp.data.model.point.PointDbo
import com.example.fitnessapp.data.model.track.TrackDbo
import com.example.fitnessapp.toLatLng
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*

class TrackFragment : SupportMapFragment(), OnMapReadyCallback {

    companion object {
        const val TAG = "TRACK_FRAGMENT"
        const val TRACK_ITEM_EXTRA = ""

        fun newInstance(args: Bundle?) = TrackFragment().apply {
            arguments = args
        }
    }

    private val localRepository = FitnessApp.INSTANCE.localRepository
    private val toastProvider = FitnessApp.INSTANCE.toastProvider
    private var track: TrackDbo? = null
    private val points = mutableListOf<PointDbo>()
    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_track, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            track = it.getParcelable(TRACK_ITEM_EXTRA)
        }
        initMap(view, savedInstanceState)
    }

    private fun initMap(v: View, savedInstanceState: Bundle?){
        mapView = v.findViewById(R.id.map)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        getTrackPoints().onSuccess {
            googleMap = map
            val polylinePoints = mutableListOf<LatLng>()
            for (point in points) {
                polylinePoints.add(point.toLatLng())
            }
            googleMap.addPolyline(
                PolylineOptions().addAll(polylinePoints)
                    .color(Color.RED)
                    .width(200f)
            )
        }
    }

    private fun getTrackPoints(): Task<Unit> {
        return localRepository.getTrackPoints(track!!.id).continueWith { task ->
            if (task.error != null) {
                toastProvider.showErrorMessage(error = task.error.message.toString())
            } else {
                points.clear()
                points.addAll(task.result)
            }
        }
    }


}