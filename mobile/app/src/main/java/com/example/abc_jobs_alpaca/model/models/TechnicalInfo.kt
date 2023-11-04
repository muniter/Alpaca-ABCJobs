package com.example.abc_jobs_alpaca.model.models

import org.json.JSONObject


data class TechnicalInfoItemResponse(
    val success: Boolean,
    val data: TechnicalInfoItem
)

data class TechnicalInfoItem(
    val description: String,
    val id: Int,
    val id_persona: Int,
    val type: TechnicalInfoType
)


data class TechnicalInfoResponse(
    val success: Boolean,
    val data: List<TechnicalInfoItem>?
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

        return TechnicalInfoItemResponse(success, TechnicalInfoItem(description, id, id_persona, type))
    }

    return TechnicalInfoItemResponse(success, TechnicalInfoItem("", 0, 0, TechnicalInfoType(0, "")))
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

fun TechnicalInfoResponse(response: JSONObject): TechnicalInfoResponse {
    val success = response.optBoolean("success", false)
    val dataObject = response.optJSONArray("data")

    val types = mutableListOf<TechnicalInfoItem>()
    if (dataObject != null) {
        for (i in 0 until dataObject.length()) {
            val typeObject = dataObject.optJSONObject(i)
            if (typeObject != null) {
                val description = typeObject.optString("description")
                val id = typeObject.optInt("id")
                val id_persona = typeObject.optInt("id_persona")
                val typeObject = typeObject.optJSONObject("type")
                val type = if (typeObject != null) {
                    val id = typeObject.optInt("id")
                    val name = typeObject.optString("name")
                    TechnicalInfoType(id, name)
                } else {
                    TechnicalInfoType(0, "")
                }
                types.add(TechnicalInfoItem(description, id, id_persona, type))
            }
        }
    }

    return TechnicalInfoResponse(success, types)
}


fun deserializeTechnicalInfo(response: JSONObject): TechnicalInfoResponse {
    val success = response.optBoolean("success", false)
    val dataObject = response.optJSONArray("data")

    val types = mutableListOf<TechnicalInfoItem>()
    if (dataObject != null) {
        for (i in 0 until dataObject.length()) {
            val typeObject = dataObject.optJSONObject(i)
            if (typeObject != null) {
                val description = typeObject.optString("description")
                val id = typeObject.optInt("id")
                val id_persona = typeObject.optInt("id_persona")
                val typeObject = typeObject.optJSONObject("type")
                val type = if (typeObject != null) {
                    val id = typeObject.optInt("id")
                    val name = typeObject.optString("name")
                    TechnicalInfoType(id, name)
                } else {
                    TechnicalInfoType(0, "")
                }
                types.add(TechnicalInfoItem(description, id, id_persona, type))
            }
        }
    }

    return TechnicalInfoResponse(success, types)
}

fun deserializeTechnicalInfoError(response: JSONObject): Exception {
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


data class TechnicalInfoItemDeleteResponse(
    val success: Boolean,
    val data: TechnicalInfoItem
)

fun deserializeTechnicalInfoItemDelete(response: JSONObject): TechnicalInfoItemDeleteResponse {
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

        return TechnicalInfoItemDeleteResponse(success, TechnicalInfoItem(description, id, id_persona, type))
    }

    return TechnicalInfoItemDeleteResponse(success, TechnicalInfoItem("", 0, 0, TechnicalInfoType(0, "")))
}

fun deserializeTechnicalInfoItemDeleteError(response: JSONObject): Exception {
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
