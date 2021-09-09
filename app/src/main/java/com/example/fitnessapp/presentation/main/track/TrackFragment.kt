package com.example.fitnessapp.presentation.main.track

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitnessapp.R
import com.example.fitnessapp.data.model.track.Track

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class TrackFragment : Fragment() {

    companion object {
        const val TAG = "TRACK_FRAGMENT"
        const val TRACK_ITEM = ""

        fun newInstance(args: Bundle?): TrackFragment {
            val fragment = TrackFragment()
            fragment.arguments = args
            return fragment
        }
    }
}