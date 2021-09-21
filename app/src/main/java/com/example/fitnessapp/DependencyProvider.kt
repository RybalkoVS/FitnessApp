package com.example.fitnessapp

import com.example.fitnessapp.data.network.FitnessApi
import com.example.fitnessapp.data.network.RetrofitBuilder
import com.example.fitnessapp.data.repository.LocalRepository
import com.example.fitnessapp.data.repository.RemoteRepository
import com.example.fitnessapp.presentation.PreferencesStore

object DependencyProvider {

    private val fitnessApi = RetrofitBuilder.retrofit.create(FitnessApi::class.java)
    val dateTimeFormatter = DateTimeFormatter()
    val remoteRepository = RemoteRepository(fitnessApi)
    val localRepository = LocalRepository()
    val preferencesStore = PreferencesStore()
}