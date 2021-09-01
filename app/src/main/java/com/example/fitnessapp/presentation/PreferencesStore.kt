package com.example.fitnessapp.presentation

import android.content.Context

class PreferencesStore(val context: Context) {

    companion object {
        const val APP_PREFERENCES = "APP_PREFERENCES"
        const val AUTHORIZATION_TOKEN = "AUTHORIZATION_TOKEN"
        const val EMPTY_STRING = ""
    }

    fun getAuthorizationToken(): String? {
        val preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        return preferences.getString(AUTHORIZATION_TOKEN, EMPTY_STRING)
    }

}