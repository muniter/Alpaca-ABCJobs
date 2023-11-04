package com.example.abc_jobs_alpaca.model.models

import org.json.JSONObject


data class TechnicalInfoItemResponse(
    val success: Boolean,
    val data: Data
)

data class Data(
    val description: String,
    val id: Int,
    val id_persona: Int,
    val type: TechnicalInfoType
)

fun deserializeTechnicalInfoItem(response: JSONObject): TechnicalInfoItemResponse {
    val success = response.optBoolean("success", false)
    val dataObject = response.optJSONObject("data")

    if (dataObject != null) {
        val description = dataObject.optString("description")
        val id = dataObject.optInt("id")
        val id_persona = dataObject.optInt("id_persona")
        val typeObject = dataObject.optJSONObject("type")
        val type = if (typeObject != null) {
            val id = typeObject.optInt("id")
            val name = typeObject.optString("name")
            TechnicalInfoType(id, name)
        } else {
            TechnicalInfoType(0, "")
        }

        return TechnicalInfoItemResponse(success, Data(description, id, id_persona, type))
    }

    return TechnicalInfoItemResponse(success, Data("", 0, 0, TechnicalInfoType(0, "")))
}


fun deserializeTechnicalInfoItemError(response: JSONObject): Exception {
    val success = response.optBoolean("success", false)

    if (!success) {
        val errorsObject = response.optJSONObject("errors")
        if (errorsObject != null) {
            val emailError = errorsObject.optString("")
            if (emailError.isNotBlank()) {
                return Exception(emailError)
            }
        }
    }
    return Exception("Error en la solicitud")
}

