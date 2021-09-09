package com.example.fitnessapp.data.model.track

import com.google.gson.annotations.SerializedName

data class TrackRequest(
    @SerializedName("token")
    val token: String? = null
)