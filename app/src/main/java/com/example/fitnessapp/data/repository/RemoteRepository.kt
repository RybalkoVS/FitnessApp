package com.example.fitnessapp.data.repository

import bolts.Task
import com.example.fitnessapp.data.model.login.LoginRequest
import com.example.fitnessapp.data.model.login.LoginResponse
import com.example.fitnessapp.data.model.point.PointRequest
import com.example.fitnessapp.data.model.point.PointResponse
import com.example.fitnessapp.data.model.registration.RegistrationRequest
import com.example.fitnessapp.data.model.registration.RegistrationResponse
import com.example.fitnessapp.data.model.track.TrackRequest
import com.example.fitnessapp.data.model.track.TrackResponse
import com.example.fitnessapp.data.network.FitnessApi

class RemoteRepository(private val fitnessApi: FitnessApi) {

    fun register(registerRequest: RegistrationRequest): Task<RegistrationResponse> {
        return Task.callInBackground {
            fitnessApi.register(registerRequest = registerRequest).execute().body()
        }
    }

    fun login(loginRequest: LoginRequest): Task<LoginResponse> {
        return Task.callInBackground {
            fitnessApi.login(loginRequest = loginRequest).execute().body()
        }
    }

    fun getTracks(trackRequest: TrackRequest): Task<TrackResponse> {
        return Task.callInBackground {
            fitnessApi.getTracks(trackRequest = trackRequest).execute().body()
        }
    }

    fun getTrackPoints(pointRequest: PointRequest): Task<PointResponse> {
        return Task.callInBackground {
            fitnessApi.getTrackPoints(pointRequest = pointRequest).execute().body()
        }
    }

}