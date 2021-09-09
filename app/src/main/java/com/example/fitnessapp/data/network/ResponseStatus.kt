package com.example.fitnessapp.data.network

enum class ResponseStatus {
    OK {
        override fun toString(): String {
            return super.toString().lowercase()
        }
    },
    ERROR {
        override fun toString(): String {
            return super.toString().lowercase()
        }
    }
}