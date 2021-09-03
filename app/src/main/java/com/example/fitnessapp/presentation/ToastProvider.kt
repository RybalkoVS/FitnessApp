package com.example.fitnessapp.presentation

import android.content.Context
import android.widget.Toast

class ToastProvider(private val context: Context) {

    fun showErrorMessage(error: String) {
        Toast.makeText(
            context,
            error,
            Toast.LENGTH_SHORT
        ).show()
    }
}