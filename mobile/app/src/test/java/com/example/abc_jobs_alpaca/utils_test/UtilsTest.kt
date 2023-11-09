package com.example.abc_jobs_alpaca.utils_test

import com.example.abc_jobs_alpaca.utils.Utils
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test

class UtilsTest {
    private val testKey = "testKey"
    private val testVal = "testVal"
    private val testIntVal = 1310

    @Test
    fun optNullableStringTestNull() {
        var testObject = JSONObject("{\"$testKey\" : null }")

        Assert.assertNull(Utils.optNullableString(testObject, testKey))
    }

    @Test
    fun optNullableStringTestNonNull() {
        var testObject = JSONObject("{\"$testKey\" : \"$testVal\"}")

        Assert.assertNotNull(Utils.optNullableString(testObject, testKey))
        Assert.assertEquals(testVal, Utils.optNullableString(testObject, testKey))
    }

    @Test
    fun optNullableIntTestNull() {
        var testObject = JSONObject("{\"$testKey\" : null }")

        Assert.assertNull(Utils.optNullableInt(testObject, testKey))
    }

    @Test
    fun optNullableIntTestNonNull() {
        var testObject = JSONObject("{\"$testKey\" : \"$testIntVal\"}")

        Assert.assertNotNull(Utils.optNullableInt(testObject, testKey))
        Assert.assertEquals(testIntVal, Utils.optNullableInt(testObject, testKey))
    }
}