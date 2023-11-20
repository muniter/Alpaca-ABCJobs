package com.example.abc_jobs_alpaca.utils_test

import android.text.format.Time
import com.example.abc_jobs_alpaca.utils.DateUtils
import com.example.abc_jobs_alpaca.utils.DateUtils.dateFormatted
import com.example.abc_jobs_alpaca.utils.DateUtils.timeFormatted
import com.example.abc_jobs_alpaca.utils.Validators
import junit.framework.TestCase.assertEquals
import org.junit.Assert
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date

class DateTimeFormatterTest {

    private val date = SimpleDateFormat("yyyy-MM-dd").parse("2023-11-19")

    @Test
    fun testDateFormatted() {
        // Arrange
        val dateFormat1 = "dd/MM/yyyy"
        val dateFormat2 = "dd-MM-yyyy"
        val dateFormat3 = "yyyy/MM/dd"
        val dateFormat4 = "yyyy-MM-dd"

        // Act
        val result1 = dateFormatted(date, dateFormat1)
        val result2 = dateFormatted(date, dateFormat2)
        val result3 = dateFormatted(date, dateFormat3)
        val result4 = dateFormatted(date, dateFormat4)

        // Assert
        assertEquals("19/11/2023", result1)
        assertEquals("19-11-2023", result2)
        assertEquals("2023/11/19", result3)
        assertEquals("2023-11-19", result4)
    }

    @Test
    fun testTimeFormatted() {
        // Arrange
        val hours = 12
        val minutes = 30
        val time = Time()
        time.hour = hours
        time.minute = minutes
        val timeFormat1 = "24h"
        val timeFormat2 = "12h"

        // Act
        val result1 = timeFormatted(time, timeFormat1)
        val result2 = timeFormatted(time, timeFormat2)

        // Assert
        assertEquals("12:30", result1)
        assertEquals("12:30 PM", result2)
    }
}