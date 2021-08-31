package com.example.fitnessapp.presentation.authorization

import android.os.Bundle

interface AuthorizationActivityCallback {
    fun moveToLoginFragment()
    fun moveToRegisterFragment()
    fun saveEnteredData(data: Bundle?)
}