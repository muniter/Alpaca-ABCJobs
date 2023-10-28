package com.example.abc_jobs_alpaca.utils_test

import com.example.abc_jobs_alpaca.utils.DateUtils
import com.example.abc_jobs_alpaca.utils.Validators
import org.junit.Assert
import org.junit.Test

class DateUtilsTest {

    @Test
    fun testFormatTo12Hour() {
        var result = DateUtils.formatTo12Hour("17:00")
        Assert.assertEquals("5:00 PM", result)

        result = DateUtils.formatTo12Hour("07:00")
        Assert.assertEquals("7:00 AM", result)

        result = DateUtils.formatTo12Hour("12:20")
        Assert.assertEquals("12:20 PM", result)

        result = DateUtils.formatTo12Hour("00:20")
        Assert.assertEquals("12:20 AM", result)

        result = DateUtils.formatTo12Hour("invalid")
        Assert.assertEquals("invalid", result)
    }

    @Test
    fun testFormatTo24Hour() {
        var result = DateUtils.formatTo24Hour("17:00")
        Assert.assertEquals("17:00", result)

        result = DateUtils.formatTo24Hour("07:00")
        Assert.assertEquals("07:00", result)

        result = DateUtils.formatTo24Hour("00:20")
        Assert.assertEquals("00:20", result)

        result = DateUtils.formatTo24Hour("invalid")
        Assert.assertEquals("invalid", result)
    }

    @Test
    fun testFormatToDDMMYYY() {
        var result = DateUtils.formatToDDMMYYYY("03/10/2022")
        Assert.assertEquals("03/10/2022", result)

        result = DateUtils.formatToDDMMYYYY("04/14/1998")
        Assert.assertEquals("04/14/1998", result)

        result = DateUtils.formatToDDMMYYYY("31/12/0001")
        Assert.assertEquals("31/12/0001", result)

        result = DateUtils.formatToDDMMYYYY("invalid")
        Assert.assertEquals("invalid", result)
    }

    @Test
    fun testFormatToMMDDYYY() {
        var result = DateUtils.formatToMMDDYYYY("03/10/2022")
        Assert.assertEquals("10/03/2022", result)

        result = DateUtils.formatToMMDDYYYY("04/14/1998")
        Assert.assertEquals("14/04/1998", result)

        result = DateUtils.formatToMMDDYYYY("31/12/0001")
        Assert.assertEquals("12/31/0001", result)

        result = DateUtils.formatToMMDDYYYY("invalid")
        Assert.assertEquals("invalid", result)
    }
}