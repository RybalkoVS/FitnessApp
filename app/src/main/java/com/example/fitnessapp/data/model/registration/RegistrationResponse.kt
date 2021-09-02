package com.example.fitnessapp.data.model.registration

import com.google.gson.annotations.SerializedName

data class RegistrationResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("token")
    val token: String,
    @SerializedName("code")
    val errorCode: String
)