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

    fun isValidCity(city: String): Boolean {
        return city.isEmpty() || city.length in 5..255
    }

    fun isValidAddress(address: String): Boolean {
        return address.isEmpty() || address.length in 5..255
    }

    fun isValidPhone(phone: String): Boolean {
        return phone.isEmpty() || phone.length in 2..15
    }

    fun isValidBio(bio: String): Boolean {
        return bio.isEmpty() || bio.length in 10..255
    }

    fun isValidBirthDate(text: String): Boolean {
        val splitted = text.split('/')
        if(splitted.size != 3 || splitted[0].length + splitted[1].length + splitted[2].length != 8){
            return  false
        }

        return true
    }
}