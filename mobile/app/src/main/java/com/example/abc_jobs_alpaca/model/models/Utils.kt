package com.example.abc_jobs_alpaca.model.models

import org.json.JSONObject

data class AcademicInfoType(
    val id: Int,
    val name: String
)

data class AcademicInfoTypeResponse(
    val success: Boolean,
    val data: List<AcademicInfoType>
)

fun deserializeTypesTitles(response: JSONObject): AcademicInfoTypeResponse {
    val success = response.optBoolean("success", false)
    val dataObject = response.optJSONArray("data")

    val types = mutableListOf<AcademicInfoType>()
    if (dataObject != null) {
        for (i in 0 until dataObject.length()) {
            val typeObject = dataObject.optJSONObject(i)
            if (typeObject != null) {
                val id = typeObject.optInt("id")
                val name = typeObject.optString("name")
                types.add(AcademicInfoType(id, name))
            }
        }
    }

    return AcademicInfoTypeResponse(success, types)
}

fun deserializeTypesTitlesError(response: JSONObject): Exception {
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


data class TechnicalInfoType(
    val id: Int,
    val name: String,
)

data class TechnicalInfoTypeResponse(
    val success: Boolean,
    val data: List<TechnicalInfoType>?
)


fun deserializeTechnicalInfoTypes(response: JSONObject): TechnicalInfoTypeResponse {
    val success = response.optBoolean("success", false)
    val data = response.optJSONArray("data")

    if (success && data != null) {
        val technicalInfoTypes = mutableListOf<TechnicalInfoType>()
        for (i in 0 until data.length()) {
            val technicalInfoType = data.optJSONObject(i)
            if (technicalInfoType != null) {
                val id = technicalInfoType.optInt("id", -1)
                val name = technicalInfoType.optString("name", "")
                if (id != -1 && name.isNotBlank()) {
                    technicalInfoTypes.add(TechnicalInfoType(id, name))
                }
            }
        }
        return TechnicalInfoTypeResponse(true, technicalInfoTypes)
    }

    return TechnicalInfoTypeResponse(false, null)
}

fun deserializeTechnicalInfoTypesError(response: JSONObject): Exception {
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

    return Exception("Error al obtener los tipos de información técnica")
}

data class SkillInfoType(
    val id: Int,
    val name: String,
)

data class SkillInfoTypeResponse(
    val success: Boolean,
    val data: List<SkillInfoType>?
)

fun deserializeSkillInfoTypes(response: JSONObject): SkillInfoTypeResponse {
    val success = response.optBoolean("success", false)
    val data = response.optJSONArray("data")

    if (success && data != null) {
        val skillInfoTypes = mutableListOf<SkillInfoType>()
        for (i in 0 until data.length()) {
            val skillInfoType = data.optJSONObject(i)
            if (skillInfoType != null) {
                val id = skillInfoType.optInt("id", -1)
                val name = skillInfoType.optString("name", "")
                if (id != -1 && name.isNotBlank()) {
                    skillInfoTypes.add(SkillInfoType(id, name))
                }
            }
        }
        return SkillInfoTypeResponse(true, skillInfoTypes)
    }

    return SkillInfoTypeResponse(false, null)
}

fun deserializeSkillInfoTypesError(response: JSONObject): Exception {
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

    return Exception("Error al obtener los tipos de información técnica")
}
