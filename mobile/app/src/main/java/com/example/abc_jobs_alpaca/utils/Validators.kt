package com.example.abc_jobs_alpaca.utils

import androidx.core.util.PatternsCompat

object Validators {
    fun validateName(text: String): Boolean {
        return text.isNotEmpty() && text.length in 2..100
    }

    fun validateLastName(text: String): Boolean {
        return text.isNotEmpty() && text.length in 2..100
    }

    fun isValidEmail(email: String): Boolean {
        val pattern = PatternsCompat.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length in 8..255
    }

    fun areStringsEqual(str1: String, str2: String): Boolean {
        return str1 == str2
    }

    fun isNotEmpty(text: String): Boolean {
        return text.isNotEmpty()
    }

    fun compareTwoNumbers(first: Int?, second: Int?): Boolean {
        if (first != null) {
            return first < second!!
        }
        return false
    }
}