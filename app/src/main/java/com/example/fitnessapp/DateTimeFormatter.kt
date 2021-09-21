package com.example.fitnessapp

import java.text.SimpleDateFormat
import java.util.*

class DateTimeFormatter {

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateWithTimeFormat = SimpleDateFormat("yyyy-MM-dd' 'HH:mm", Locale.getDefault())
}