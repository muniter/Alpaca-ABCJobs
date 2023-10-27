package com.example.abc_jobs_alpaca.model.models

import org.json.JSONObject

data class ConfigRequest(
    val language: UserLanguageApp,
    val dateFormat: UserDateFormat,
    val timeFormat: UserTimeFormat
)

data class ConfigResponse(
    val success: Boolean,
    val data: ConfigData
)

data class ConfigData(
    val languageApp: UserLanguageApp,
    val timeFormat: UserTimeFormat,
    val dateFormat: UserDateFormat
)

enum class UserLanguageApp(val formatString: String) {
    ES("Español"),
    EN("English");

    fun toJsonString() = formatString
}

enum class UserDateFormat(val formatString: String) {
    AAAAMMDDSLASH("yyyy/MM/dd"),
    AAAAMMDDHYPHEN("yyyy-MM-dd"),
    DDMMAAAASLASH("dd/MM/yyyy"),
    DDMMAAAAHYPHEN("dd-MM-yyyy");

    fun toJsonString() = formatString
}

enum class UserTimeFormat(val formatString: String) {
    FORMATOHORAS12("12h"),
    FORMATOHORAS24("24h");

    fun toJsonString() = formatString
}

fun serializeConfig(configRequest: ConfigRequest): JSONObject {
    val json = JSONObject()

    val configObject = JSONObject()
    configObject.put("languageApp", configRequest.language)
    configObject.put("timeFormat", configRequest.timeFormat.formatString)
    configObject.put("dateFormat", configRequest.dateFormat.formatString)

    json.put("config", configObject)

    return json
}

