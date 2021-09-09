package com.example.fitnessapp.data.model.point

import com.google.gson.annotations.SerializedName

data class Point(
    @SerializedName("lng")
    val longitude: Double,
    @SerializedName("lat")
    val latitude: Double
)