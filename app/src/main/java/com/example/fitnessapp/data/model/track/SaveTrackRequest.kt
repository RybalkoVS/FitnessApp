package com.example.fitnessapp.data.model.track

import com.example.fitnessapp.data.model.point.PointDto
import com.google.gson.annotations.SerializedName

data class SaveTrackRequest(
    @SerializedName("token")
    val token: String? = null,
    @SerializedName("id")
    val serverId: Int? = null,
    @SerializedName("beginsAt")
    val beginTime: Long,
    @SerializedName("time")
    val duration: Long,
    @SerializedName("distance")
    val distance: Int,
    @SerializedName("points")
    val points: List<PointDto>
)