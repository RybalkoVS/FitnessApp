package com.example.fitnessapp

import android.widget.EditText
import com.example.fitnessapp.data.model.point.PointDbo
import com.google.android.gms.maps.model.LatLng

fun EditText.getValue(): String {
    return this.text.toString()
}

fun PointDbo.toLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}