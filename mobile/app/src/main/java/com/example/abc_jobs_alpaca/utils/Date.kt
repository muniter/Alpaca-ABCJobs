package com.example.abc_jobs_alpaca.utils

class Date {
    fun formatTo24Hour(time: String): String {
        val parts = time.split(":")
        if (parts.size == 2) {
            val hour = parts[0]
            val minute = parts[1]
            return "$hour:$minute" // El formato de 24 horas no cambia
        }
        return time // Devuelve sin cambios si el formato es incorrecto
    }

    fun formatTo12Hour(time: String): String {
        val parts = time.split(":")
        if (parts.size == 2) {
            val hour = parts[0].toInt()
            val minute = parts[1]
            val amPm = if (hour < 12) "AM" else "PM"
            val hour12 = if (hour == 0) 12 else if (hour <= 12) hour else hour - 12
            return "$hour12:$minute $amPm"
        }
        return time // Devuelve sin cambios si el formato es incorrecto
    }

    object DateUtils {
        fun formatToDDMMYYYY(date: String): String {
            val parts = date.split("/")
            if (parts.size == 3) {
                val day = parts[0]
                val month = parts[1]
                val year = parts[2]
                return "$day/$month/$year"
            }
            return date // Devuelve sin cambios si el formato es incorrecto
        }

        fun formatToMMDDYYYY(date: String): String {
            val parts = date.split("/")
            if (parts.size == 3) {
                val month = parts[0]
                val day = parts[1]
                val year = parts[2]
                return "$month/$day/$year"
            }
            return date // Devuelve sin cambios si el formato es incorrecto
        }
    }

}