package com.example.abc_jobs_alpaca.model.models

import org.json.JSONObject


data class CountriesResponse(
    val success: Boolean,
    val data: List<Country>?
)

data class Country (
    val num_code: Int?,
    val alpha_2_code: String,
    val alpha_3_code: String,
    val en_short_name: String,
    val nationality: String
){
    override fun toString(): String {
        return en_short_name
    }
}


fun deserializeCountries(response: JSONObject) : CountriesResponse{

    val success = response.optBoolean("success", false)
    val dataObject = response.optJSONArray("data")

    val countries = mutableListOf<Country>()
    if (dataObject != null) {
        for (i in 0 until dataObject.length()) {
            val academicInfoObject = dataObject.optJSONObject(i)
            if (academicInfoObject != null) {
                val num_code = academicInfoObject.optInt("num_code")
                val alpha_2_code = academicInfoObject.optString("alpha_2_code")
                val alpha_3_code = academicInfoObject.optString("alpha_3_code")
                val en_short_name = academicInfoObject.optString("en_short_name")
                val nationality = academicInfoObject.optString("nationality")
                countries.add(Country(num_code, alpha_2_code, alpha_3_code, en_short_name, nationality))
            }
        }
    }

    return CountriesResponse(success, countries)
}

fun deserializeCountriesError(response: JSONObject): Exception {
    val success = response.optBoolean("success", false)

    if (!success) {
        val errorsObject = response.optJSONObject("errors")
        if (errorsObject != null) {
           return Exception(errorsObject.toString())
        }
    }
    return Exception("Error en la solicitud")
}