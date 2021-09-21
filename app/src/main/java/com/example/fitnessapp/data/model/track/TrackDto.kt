package com.example.fitnessapp.data.model.track

import com.google.gson.annotations.SerializedName


data class TrackDto(
    @SerializedName("id")
    val serverId: Int? = null,
    @SerializedName("beginsAt")
    val beginTime: Long,
    @SerializedName("time")
    val duration: Long,
    @SerializedName("distance")
    val distance: Int
)