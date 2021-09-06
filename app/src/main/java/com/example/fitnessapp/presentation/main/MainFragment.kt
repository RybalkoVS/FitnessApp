package com.example.fitnessapp.presentation.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.fitnessapp.R


class MainFragment : Fragment(R.layout.fragment_main) {

    companion object {

        const val TAG = "MAIN_FRAGMENT"

        fun newInstance() = MainFragment()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}