package com.example.fitnessapp.presentation.main.track

import android.os.Bundle
import androidx.fragment.app.Fragment

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