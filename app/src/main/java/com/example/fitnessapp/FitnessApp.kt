package com.example.fitnessapp

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import com.example.fitnessapp.data.database.Db
import com.example.fitnessapp.data.network.FitnessApi
import com.example.fitnessapp.data.network.RetrofitBuilder
import com.example.fitnessapp.data.repository.LocalRepository
import com.example.fitnessapp.data.repository.RemoteRepository
import com.example.fitnessapp.presentation.PreferencesStore
import com.example.fitnessapp.presentation.ToastProvider

class FitnessApp : Application() {

    companion object {
        lateinit var INSTANCE: FitnessApp
    }

    lateinit var fitnessApi: FitnessApi
    lateinit var remoteRepository: RemoteRepository
    lateinit var localRepository: LocalRepository
    lateinit var preferencesStore: PreferencesStore
    lateinit var toastProvider: ToastProvider
    lateinit var database: SQLiteDatabase

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this

        fitnessApi = RetrofitBuilder.retrofit.create(FitnessApi::class.java)
        remoteRepository = RemoteRepository(fitnessApi)
        localRepository = LocalRepository()
        preferencesStore = PreferencesStore(applicationContext)
        toastProvider = ToastProvider(applicationContext)
        database = Db(this).writableDatabase
    }

}