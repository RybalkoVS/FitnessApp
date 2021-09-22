package com.example.fitnessapp

import com.example.fitnessapp.data.network.FitnessApi
import com.example.fitnessapp.data.network.RetrofitBuilder
import com.example.fitnessapp.data.repository.LocalRepository
import com.example.fitnessapp.data.repository.RemoteRepository
import com.example.fitnessapp.data.repository.PreferencesRepository

object DependencyProvider {

    private val fitnessApi = RetrofitBuilder.retrofit.create(FitnessApi::class.java)
    val remoteRepository = RemoteRepository(fitnessApi)
    val localRepository = LocalRepository()
    val preferencesRepository = PreferencesRepository()
}