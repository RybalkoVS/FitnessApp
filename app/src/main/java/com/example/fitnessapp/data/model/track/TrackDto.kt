package com.example.fitnessapp.data.model.track

import com.google.gson.annotations.SerializedName


data class TrackDto(
    @SerializedName("id")
    var serverId: Int,
    @SerializedName("beginsAt")
    val beginTime: Long,
    @SerializedName("time")
    val duration: Long,
    @SerializedName("distance")
    val distance: Int
)