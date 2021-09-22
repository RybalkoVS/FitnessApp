package com.example.fitnessapp

import java.text.SimpleDateFormat
import java.util.*

object DateTimeFormatter {

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
    val dateWithTimeFormat = SimpleDateFormat("yyyy-MM-dd' 'HH:mm", Locale.US)
}