package com.example.fitnessapp.ui.authorization

import android.os.Bundle

interface AuthorizationActivityCallback {
    fun moveToLoginFragment()
    fun moveToRegisterFragment()
    fun saveEnteredData(data: Bundle?)
}