package com.example.fitnessapp

import android.widget.EditText

fun EditText.getValue(): String {
    return this.text.toString()
}

fun Int.toBoolean(): Boolean {
    return this > 0
}
