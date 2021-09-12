package com.example.fitnessapp.data.model.track

import com.google.gson.annotations.SerializedName

data class SaveTrackResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("id")
    val serverId: Int,
    @SerializedName("code")
    val errorCode: String
)