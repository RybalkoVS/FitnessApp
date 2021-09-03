package com.example.fitnessapp

import android.app.Application
import com.example.fitnessapp.data.network.FitnessApi
import com.example.fitnessapp.data.repository.RemoteRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FitnessApp : Application() {

    companion object {
        const val BASE_URL = "https://pub.zame-dev.org/senla-training-addition/"
        lateinit var INSTANCE: FitnessApp
    }

    lateinit var fitnessApi: FitnessApi
    lateinit var remoteRepository: RemoteRepository

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this

        configureRetrofit()
        remoteRepository = RemoteRepository(fitnessApi)
    }

    private fun configureRetrofit() {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        fitnessApi = retrofit.create(FitnessApi::class.java)
    }
}