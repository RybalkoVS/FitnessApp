package com.example.fitnessapp.data.network

import com.example.fitnessapp.data.model.registration.RegistrationRequest
import com.example.fitnessapp.data.model.registration.RegistrationResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface FitnessApi {

    @POST("lesson-26.php?method=register")
    fun register(@Body registerRequest: RegistrationRequest): Call<RegistrationResponse>

}