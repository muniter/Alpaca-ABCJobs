package com.example.abc_jobs_alpaca.model.models

import org.json.JSONArray
import org.json.JSONObject

data class WorkInfoRequest(
    val role: String,
    val company: String,
    val description: String,
    val skills: List<Int>,
    val start_year: Int,
    val end_year: Int, )

data class WorkInfoItem(
    val role: String,
    val company: String,
    val description: String,
    val skills: List<SkillInfoType>,
    val startYear: Int,
    val endYear: Int?,
    val id: Int,
    val idPersona: Int
)
data class WorkInfoItemResponse(
    val success: Boolean,
    val data: WorkInfoItem?
)

data class WorkInfoResponse(
    val success: Boolean,
    val data: List<WorkInfoItem>?
)

data class WorkInfoItemDeleteResponse(
    val success: Boolean,
    val data: WorkInfoItem?
)


fun deserializeWorkInfoItem(response: JSONObject): WorkInfoItemResponse {
    val success = response.optBoolean("success", false)
    val dataObject = response.optJSONObject("data")

    if (dataObject != null) {
        val role = dataObject.optString("role")
        val company = dataObject.optString("company")
        val description = dataObject.optString("description")
        val startYear = dataObject.optInt("start_year")
        val endYear = dataObject.optInt("end_year")
        val id = dataObject.optInt("id")
        val idPersona = dataObject.optInt("id_persona")
        val skillsObject = dataObject.optJSONArray("skills")
        val skills = mutableListOf<SkillInfoType>()
        if (skillsObject != null) {
            for (i in 0 until skillsObject.length()) {
                val skillObject = skillsObject.optJSONObject(i)
                if (skillObject != null) {
                    val id = skillObject.optInt("id")
                    val name = skillObject.optString("name")
                    skills.add(SkillInfoType(id, name))
                }
            }
        }

        return WorkInfoItemResponse(success, WorkInfoItem(role, company, description, skills, startYear, endYear, id, idPersona))
    }

    return WorkInfoItemResponse(success, WorkInfoItem("", "", "", mutableListOf(), 0, 0, 0, 0))
}

fun deserializeWorkInfoItemError(response: JSONObject): Exception {
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


fun deserializeWorkInfo(response: JSONObject): WorkInfoResponse {
    val success = response.optBoolean("success", false)
    val dataObject = response.optJSONArray("data")

    val workInfo = mutableListOf<WorkInfoItem>()
    if (dataObject != null) {
        for (i in 0 until dataObject.length()) {
            val workInfoObject = dataObject.optJSONObject(i)
            if (workInfoObject != null) {
                val role = workInfoObject.optString("role")
                val company = workInfoObject.optString("company")
                val description = workInfoObject.optString("description")
                val startYear = workInfoObject.optInt("start_year")
                val endYear = workInfoObject.optInt("end_year")
                val id = workInfoObject.optInt("id")
                val idPersona = workInfoObject.optInt("id_persona")
                val skillsObject = workInfoObject.optJSONArray("skills")
                val skills = mutableListOf<SkillInfoType>()
                if (skillsObject != null) {
                    for (i in 0 until skillsObject.length()) {
                        val skillObject = skillsObject.optJSONObject(i)
                        if (skillObject != null) {
                            val id = skillObject.optInt("id")
                            val name = skillObject.optString("name")
                            skills.add(SkillInfoType(id, name))
                        }
                    }
                }
                workInfo.add(WorkInfoItem(role, company, description, skills, startYear, endYear, id, idPersona))
            }
        }
    }

    return WorkInfoResponse(success, workInfo)
}

fun deserializeWorkInfoItemDelete(response: JSONObject): WorkInfoItemDeleteResponse {
    val success = response.optBoolean("success", false)
    val dataObject = response.optJSONObject("data")

    if (dataObject != null) {
        val role = dataObject.optString("role")
        val company = dataObject.optString("company")
        val description = dataObject.optString("description")
        val startYear = dataObject.optInt("start_year")
        val endYear = dataObject.optInt("end_year")
        val id = dataObject.optInt("id")
        val idPersona = dataObject.optInt("id_persona")
        val skillsObject = dataObject.optJSONArray("skills")
        val skills = mutableListOf<SkillInfoType>()
        if (skillsObject != null) {
            for (i in 0 until skillsObject.length()) {
                val skillObject = skillsObject.optJSONObject(i)
                if (skillObject != null) {
                    val id = skillObject.optInt("id")
                    val name = skillObject.optString("name")
                    skills.add(SkillInfoType(id, name))
                }
            }
        }

        return WorkInfoItemDeleteResponse(success, WorkInfoItem(role, company, description, skills, startYear, endYear, id, idPersona))
    }

    return WorkInfoItemDeleteResponse(success, WorkInfoItem("", "", "", mutableListOf(), 0, 0, 0, 0))
}


fun deserializeWorkInfoItemDeleteError(response: JSONObject): Exception {
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


fun serializeWorkInfo(workInfo: WorkInfoRequest): JSONObject {
    val jsonObject = JSONObject()
    jsonObject.put("role", workInfo.role)
    jsonObject.put("company", workInfo.company)
    jsonObject.put("description", workInfo.description)
    jsonObject.put("skills", JSONArray(workInfo.skills))
    jsonObject.put("start_year", workInfo.start_year)
    jsonObject.put("end_year", workInfo.end_year)
    return jsonObject
}