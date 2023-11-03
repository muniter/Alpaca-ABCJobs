package com.example.abc_jobs_alpaca.utils

import org.json.JSONObject

object Utils {
    fun optNullableString(json: JSONObject, key: String?): String? {

        return if (json.isNull(key))
            null;
        else
            json.optString(key, null);
    }

    fun optNullableInt(json: JSONObject, key: String?): Int? {

        return if (json.isNull(key))
            null;
        else
            json.optInt(key, 0);
    }
}
