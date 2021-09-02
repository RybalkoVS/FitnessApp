package com.example.fitnessapp.presentation

import android.content.Context
import android.content.SharedPreferences

class PreferencesStore(private val context: Context) {

    companion object {
        const val APP_PREFERENCES = "APP_PREFERENCES"
        const val AUTHORIZATION_TOKEN = "AUTHORIZATION_TOKEN"
        const val EMPTY_STRING = ""
    }

    private lateinit var preferences: SharedPreferences

    fun getAuthorizationToken(): String? {
        preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        return preferences.getString(AUTHORIZATION_TOKEN, EMPTY_STRING)
    }

    fun saveAuthorizationToken(token: String) {
        preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        preferences.edit().apply {
            putString(AUTHORIZATION_TOKEN, token)
            apply()
        }
    }

    fun clearAuthorizationToken() {
        preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        preferences.edit().apply() {
            putString(AUTHORIZATION_TOKEN, EMPTY_STRING)
            apply()
        }
    }

}