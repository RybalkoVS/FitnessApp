package com.example.fitnessapp.presentation

import android.content.Context
import android.content.SharedPreferences

class PreferencesStore {

    companion object {
        private const val APP_PREFERENCES = "APP_PREFERENCES"
        private const val AUTHORIZATION_TOKEN = "AUTHORIZATION_TOKEN"
        private const val PERMISSION_DENIED_COUNTER = "PERMISSION_DENIED_TWICE"
        private const val IS_TOKEN_EXPIRED = "IS_TOKEN_EXPIRED"
        private const val EMPTY_STRING = ""
        private const val DEFAULT_INT_VALUE = 0
        private const val MAX_PERMISSION_DENY_COUNT = 2
    }

    private lateinit var preferences: SharedPreferences

    fun getAuthorizationToken(context: Context): String? {
        preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        return preferences.getString(AUTHORIZATION_TOKEN, EMPTY_STRING)
    }

    fun saveAuthorizationToken(context: Context, token: String) {
        preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        preferences.edit().apply {
            putString(AUTHORIZATION_TOKEN, token)
            apply()
        }
    }

    fun setTokenExpired(context: Context) {
        preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        preferences.edit().apply {
            putBoolean(IS_TOKEN_EXPIRED, true)
            apply()
        }
    }

    fun isTokenExpired(context: Context): Boolean {
        preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        return preferences.getBoolean(IS_TOKEN_EXPIRED, false)
    }

    fun setTokenValid(context: Context) {
        preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        preferences.edit().apply {
            putBoolean(IS_TOKEN_EXPIRED, false)
            apply()
        }
    }

    fun clearAuthorizationToken(context: Context) {
        preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        preferences.edit().apply {
            putString(AUTHORIZATION_TOKEN, EMPTY_STRING)
            apply()
        }
    }

    fun updateDeniedLocationPermissionCounter(context: Context) {
        preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        var counter = preferences.getInt(PERMISSION_DENIED_COUNTER, DEFAULT_INT_VALUE)
        counter++
        preferences.edit().apply {
            putInt(PERMISSION_DENIED_COUNTER, counter)
            apply()
        }
    }

    fun isLocationPermissionDeniedTwice(context: Context): Boolean {
        preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val denyCount = preferences.getInt(PERMISSION_DENIED_COUNTER, DEFAULT_INT_VALUE)
        return denyCount == MAX_PERMISSION_DENY_COUNT
    }

}