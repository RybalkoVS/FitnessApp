package com.example.fitnessapp.data.network

import com.example.fitnessapp.data.model.login.LoginRequest
import com.example.fitnessapp.data.model.login.LoginResponse
import com.example.fitnessapp.data.model.point.PointRequest
import com.example.fitnessapp.data.model.point.PointResponse
import com.example.fitnessapp.data.model.registration.RegistrationRequest
import com.example.fitnessapp.data.model.registration.RegistrationResponse
import com.example.fitnessapp.data.model.track.TrackRequest
import com.example.fitnessapp.data.model.track.TrackResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface FitnessApi {

    @POST("lesson-26.php?method=register")
    fun register(@Body registerRequest: RegistrationRequest): Call<RegistrationResponse>

    @POST("lesson-26.php?method=login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("lesson-26.php?method=tracks")
    fun getTracks(@Body trackRequest: TrackRequest): Call<TrackResponse>

    @POST("lesson-26.php?method=points")
    fun getTrackPoints(@Body pointRequest: PointRequest): Call<PointResponse>
}