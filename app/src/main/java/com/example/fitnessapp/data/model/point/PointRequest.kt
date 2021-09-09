package com.example.fitnessapp.data.model.point

import com.google.gson.annotations.SerializedName

data class PointRequest(
    @SerializedName("token")
    val token: String,
    @SerializedName("id")
    val trackId: Int
)