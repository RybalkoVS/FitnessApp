package com.example.fitnessapp.data.model.notification

import java.text.SimpleDateFormat
import java.util.*

data class Notification(
    val id: Int,
    val date: Long
) {

    val time: String
        get() {
            val sdf = SimpleDateFormat("yyyy-MM-dd' 'HH:mm", Locale.getDefault())
            val date = Date(this.date)
            return sdf.format(date)
        }

    val dateInDateFormat: String
        get() {
            val sdf = SimpleDateFormat("'HH:mm", Locale.getDefault())
            val date = Date(this.date)
            return sdf.format(date)
        }

}