package com.example.abc_jobs_alpaca.model.models

import com.example.abc_jobs_alpaca.utils.Utils
import org.json.JSONObject
import java.util.Date

data class PersonalInfoResponse(
    val success: Boolean,
    val data: PersonalInfo
)

data class PersonalInfoRequest(
    var birth_date: String?,
    var country_code: Int?,
    var city: String?,
    var address: String?,
    var phone: String?,
    var biography: String?,
    var languages: List<Language>?
)

data class PersonalInfo(
    var names: String?,
    var last_names: String?,
    var full_name: String?,
    var email: String?,
    var birth_date: Date?,
    var country_code: Int?,
    var country: String?,
    var city: String?,
    var address: String?,
    var phone: String?,
    var biography: String?,
    var languages: List<Language>?
)

data class Language(
    val id: String,
    val name: String
)

fun deserializePersonalInfo(response: JSONObject): PersonalInfoResponse? {
    val success = response.optBoolean("success", false)
    val dataObject = response.optJSONObject("data")

    if (dataObject != null) {
        val stringDate = Utils.optNullableString(dataObject, "birth_date")
        val splitDate = stringDate?.split("-")

        val names = Utils.optNullableString(dataObject, "names")
        val last_names = Utils.optNullableString(dataObject, "last_names")
        val full_name = Utils.optNullableString(dataObject, "full_name")
        val email = Utils.optNullableString(dataObject, "email")
        val birth_date = if (stringDate == null || splitDate?.size != 3) null else Date(splitDate[0].toInt() - 1900, splitDate[1].toInt() - 1, splitDate[2].toInt())
        val country_code = Utils.optNullableInt(dataObject,"country_code")
        val country = Utils.optNullableString(dataObject, "country")
        val city = Utils.optNullableString(dataObject, "city")
        val address = Utils.optNullableString(dataObject, "address")
        val phone = Utils.optNullableString(dataObject, "phone")
        val biography = Utils.optNullableString(dataObject, "biography")
        val languages = dataObject.optJSONArray("languages")

        var parsedLanguages: MutableList<Language>? = null
        if (languages != null) {
            parsedLanguages = mutableListOf()
            for (i in 0 until languages.length()) {
                val language = languages.optJSONObject(i)

                parsedLanguages.add(
                    Language(
                        language.optString("id"),
                        language.optString("name")
                    )
                )
            }
        }

        val personalInfo = PersonalInfo(
            names,
            last_names,
            full_name,
            email,
            birth_date,
            country_code,
            country,
            city,
            address,
            phone,
            biography,
            parsedLanguages
        )

        return PersonalInfoResponse(success, personalInfo)
    }

    return null
}

fun deserializePersonalInfoError(response: JSONObject): Exception {
    val success = response.optBoolean("success", false)

    if (!success) {
        val errorsObject = response.optJSONObject("errors")
        if (errorsObject != null) {
            val error = errorsObject.optString("")
            if (error.isNotBlank()) {
                return Exception(error)
            }
        }
    }
    return Exception("Error en la solicitud")
}

fun serializePersonalInfo(personalInfoRequest: PersonalInfoRequest): JSONObject {

    val json = JSONObject()
    json.put("birth_date", if (personalInfoRequest.birth_date == null) JSONObject.NULL else personalInfoRequest.birth_date)
    json.put("country_code", if (personalInfoRequest.country_code == null) JSONObject.NULL else personalInfoRequest.country_code)
    json.put("city", if (personalInfoRequest.city == null) JSONObject.NULL else personalInfoRequest.city)
    json.put("address", if (personalInfoRequest.address == null) JSONObject.NULL else personalInfoRequest.address)
    json.put("phone", if (personalInfoRequest.phone == null) JSONObject.NULL else personalInfoRequest.phone)
    json.put("biography", if (personalInfoRequest.biography == null) JSONObject.NULL else personalInfoRequest.biography)
    json.put("languages", if (personalInfoRequest.languages == null) JSONObject.NULL else personalInfoRequest.languages)

    return json
}
