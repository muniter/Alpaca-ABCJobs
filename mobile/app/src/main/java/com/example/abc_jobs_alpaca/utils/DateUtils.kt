package com.example.abc_jobs_alpaca.utils

import android.text.format.Time
import com.example.abc_jobs_alpaca.view.PreferencesFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private const val DATE_FORMAT_1 = "dd/MM/yyyy"
    private const val DATE_FORMAT_2 = "dd-MM-yyyy"
    private const val DATE_FORMAT_3 = "yyyy/MM/dd"
    private const val DATE_FORMAT_4 = "yyyy-MM-dd"
    private const val TIME_FORMAT_2 = "24h"


    fun dateFormatted(date: Date, dateFormat: String): String {
        val dateString: String;
        when (dateFormat) {
            DATE_FORMAT_1 -> dateString =
                SimpleDateFormat(DATE_FORMAT_1, Locale.getDefault()).format(date)

            DATE_FORMAT_2 -> dateString =
                SimpleDateFormat(DATE_FORMAT_2, Locale.getDefault()).format(date)

            DATE_FORMAT_3 -> dateString =
                SimpleDateFormat(DATE_FORMAT_3, Locale.getDefault()).format(date)

            DATE_FORMAT_4 -> dateString =
                SimpleDateFormat(DATE_FORMAT_4, Locale.getDefault()).format(date)

            else -> dateString = ""
        }
        return dateString
    }

    fun timeFormatted(time: Time, timeFormat: String): String {
        val timeString: String
        val formatPattern = if (timeFormat == TIME_FORMAT_2) {
            "HH:mm"
        } else {
            "hh:mm a"
        }

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, time.hour)
        calendar.set(Calendar.MINUTE, time.minute)

        timeString = SimpleDateFormat(formatPattern, Locale.getDefault()).format(calendar.time)

        return timeString
    }
}

