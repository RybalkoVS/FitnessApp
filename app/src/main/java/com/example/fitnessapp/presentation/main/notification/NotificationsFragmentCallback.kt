package com.example.fitnessapp.presentation.main.notification

import java.util.*

interface NotificationsFragmentCallback {

    fun addNotification(calendar: Calendar)
    fun editNotification(calendar: Calendar)
}