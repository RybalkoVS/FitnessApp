package com.example.fitnessapp.data.model.track

import com.google.gson.annotations.SerializedName

data class TrackResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("tracks")
    val trackList: MutableList<TrackDto>,
    @SerializedName("code")
    val errorCode: String
)