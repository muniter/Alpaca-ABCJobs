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

data class AcademicInfoItemResponse(
    val success: Boolean,
    val data: AcademicInfoItem?
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


data class AcademicInfoItemDeleteResponse(
    val success: Boolean,
    val data: AcademicInfoItem?
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


fun deserializeAcademicInfoItem(reponse: JSONObject): AcademicInfoItemResponse{
    val success = reponse.optBoolean("success", false)
    val dataObject = reponse.optJSONObject("data")

    val academicInfoItem = dataObject?.let {
        val institution = it.optString("institution")
        val title = it.optString("title")
        val startYear = it.optInt("start_year")
        val endYear = it.optInt("end_year")
        val achievement = it.optString("achievement")
        val id = it.optInt("id")
        val idPersona = it.optInt("id_persona")
        val typeObject = it.optJSONObject("type")
        val type = if (typeObject != null) {
            val id = typeObject.optInt("id")
            val name = typeObject.optString("name")
            AcademicInfoType(id, name)
        } else {
            AcademicInfoType(0, "")
        }
        AcademicInfoItem(institution, title, startYear, endYear, achievement, id, idPersona, type)
    } ?: AcademicInfoItem("", "", 0, 0, "", 0, 0, AcademicInfoType(0, ""))
    return AcademicInfoItemResponse(success, academicInfoItem)
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

fun deserializeAcademicInfoItemDelete(response: JSONObject): AcademicInfoItemDeleteResponse {
    val success = response.optBoolean("success", false)
    val dataObject = response.optJSONObject("data")

    val academicInfoItem = dataObject?.let {
        val institution = it.optString("institution")
        val title = it.optString("title")
        val startYear = it.optInt("start_year")
        val endYear = it.optInt("end_year")
        val achievement = it.optString("achievement")
        val id = it.optInt("id")
        val idPersona = it.optInt("id_persona")
        val typeObject = it.optJSONObject("type")
        val type = if (typeObject != null) {
            val id = typeObject.optInt("id")
            val name = typeObject.optString("name")
            AcademicInfoType(id, name)
        } else {
            AcademicInfoType(0, "")
        }
        AcademicInfoItem(institution, title, startYear, endYear, achievement, id, idPersona, type)
    } ?: AcademicInfoItem("", "", 0, 0, "", 0, 0, AcademicInfoType(0, ""))
    return AcademicInfoItemDeleteResponse(success, academicInfoItem)
}

fun deserializeAcademicInfoItemDeleteError(response: JSONObject): Exception {
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