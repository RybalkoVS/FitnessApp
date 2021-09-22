package com.example.fitnessapp

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.fitnessapp.data.model.point.PointDbo
import com.example.fitnessapp.data.model.point.PointDto
import com.google.android.gms.maps.model.LatLng

fun EditText.getValue(): String {
    return this.text.toString()
}

fun PointDbo.toLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}

fun Context.showMessage(message: String) {
    Toast.makeText(
        this,
        message,
        Toast.LENGTH_SHORT
    ).show()
}

fun View.setVisible() {
    this.visibility = View.VISIBLE
}

fun View.setInvisible() {
    this.visibility = View.INVISIBLE
}

fun Button.setDisabled() {
    this.isEnabled = false
}

fun Button.setEnabled() {
    this.isEnabled = true
}

fun PointDbo.toPointDto(): PointDto {
    return PointDto(this.longitude, this.latitude)
}