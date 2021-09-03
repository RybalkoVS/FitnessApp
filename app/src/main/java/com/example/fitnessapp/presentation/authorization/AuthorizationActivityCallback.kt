package com.example.fitnessapp.presentation.authorization

import android.os.Bundle
import androidx.fragment.app.Fragment

interface AuthorizationActivityCallback {

    fun showFragment(fragment: Fragment, fragmentTag: String)
    fun saveEnteredData(data: Bundle?)
    fun closeActivity()
}