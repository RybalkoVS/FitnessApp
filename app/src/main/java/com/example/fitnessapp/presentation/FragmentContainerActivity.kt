package com.example.fitnessapp.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment

interface FragmentContainerActivity {

    fun showFragment(fragmentTag: String, args: Bundle? = null)
    fun getFragmentByTag(fragmentTag: String, args: Bundle? = null): Fragment
    fun closeActivity()
}