package com.example.abc_jobs_alpaca.model.models

import org.json.JSONObject

data class AcademicInfoRequest(
    val institution: String,
    val title: String,
    val start_year: Int,
    val end_year: Int,
    val achievement: String,
    val type: Int
)

data class AcademicInfoResponse(
    val success: Boolean,
    val data: List<AcademicInfoItem>?
)

data class AcademicInfoItem(
    val institution: String,
    val title: String,
    val start_year: Int,
    val end_year: Int,
    val achievement: String,
    val id: Int,
    val id_persona: Int,
    val type: AcademicInfoType
)

data class AcademicInfoType(
    val id: Int,
    val name: String
)

data class AcademicInfoTypeResponse(
    val success: Boolean,
    val data: List<AcademicInfoType>
)

 fun deserializeAcademicInfoError(response: JSONObject): Exception {
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


fun deserializeAcademicInfo(response: JSONObject): AcademicInfoResponse {
    val success = response.optBoolean("success", false)
    val dataObject = response.optJSONArray("data")

    val academicInfo = mutableListOf<AcademicInfoItem>()
    if (dataObject != null) {
        for (i in 0 until dataObject.length()) {
            val academicInfoObject = dataObject.optJSONObject(i)
            if (academicInfoObject != null) {
                val institution = academicInfoObject.optString("institution")
                val title = academicInfoObject.optString("title")
                val startYear = academicInfoObject.optInt("start_year")
                val endYear = academicInfoObject.optInt("end_year")
                val achievement = academicInfoObject.optString("achievement")
                val id = academicInfoObject.optInt("id")
                val idPersona = academicInfoObject.optInt("id_persona")
                val typeObject = academicInfoObject.optJSONObject("type")
                val type = if (typeObject != null) {
                    val id = typeObject.optInt("id")
                    val name = typeObject.optString("name")
                    AcademicInfoType(id, name)
                } else {
                    AcademicInfoType(0, "")
                }
                academicInfo.add(AcademicInfoItem(institution, title, startYear, endYear, achievement, id, idPersona, type))
            }
        }
    }

    return AcademicInfoResponse(success, academicInfo)
}

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


fun serializeAcademicInfo(request:  AcademicInfoRequest): JSONObject {
    val json = JSONObject()
    json.put("institution", request.institution)
    json.put("title", request.title)
    json.put("start_year", request.start_year)
    json.put("end_year", request.end_year)
    json.put("achievement", request.achievement)
    json.put("type", request.type)
    return json
}