package com.example.fitnessapp.presentation

import android.content.Context
import android.widget.Toast

class ToastProvider(private val context: Context?) {

    fun showMessage(message: String) {
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}