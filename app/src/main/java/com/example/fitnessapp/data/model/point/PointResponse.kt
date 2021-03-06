package com.example.fitnessapp.data.model.point

import com.google.gson.annotations.SerializedName

data class PointResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("points")
    val pointList: List<PointDto>,
    @SerializedName("code")
    val errorCode: String
)