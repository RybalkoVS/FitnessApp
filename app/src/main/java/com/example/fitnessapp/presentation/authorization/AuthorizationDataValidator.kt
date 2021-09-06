package com.example.fitnessapp.presentation.authorization

class AuthorizationDataValidator {

    companion object {
        const val EMAIL_PATTERN = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$"
    }

    fun isInputEmpty(vararg input: String): Boolean {
        var emptyInput = false
        for (field in input) {
            if (field.isEmpty()) {
                emptyInput = true
                break
            }
        }
        return emptyInput
    }

    fun isEmailValid(email: String): Boolean {
        val pattern = EMAIL_PATTERN.toRegex()
        return email.matches(pattern)
    }

    fun isPasswordValid(password: String, repeatPassword: String): Boolean {
        return password == repeatPassword
    }
}