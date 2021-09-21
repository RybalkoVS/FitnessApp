package com.example.fitnessapp

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import com.example.fitnessapp.data.database.Db

class FitnessApp : Application() {

    companion object {
        lateinit var INSTANCE: FitnessApp
    }

    lateinit var database: SQLiteDatabase

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this
        database = Db(this).writableDatabase
    }

}