package com.example.fitnessapp.data.model.registration

import com.google.gson.annotations.SerializedName


data class RegistrationRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("fistName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("password")
    val password: String
)