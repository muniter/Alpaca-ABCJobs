package com.example.abc_jobs_alpaca.model.models

import org.json.JSONObject

data class VacancyItem(
    val name: String,
    val description: String,
    val team: TeamItem
)

data class VacancyResponse(
    val success: Boolean,
    val data: List<VacancyItem>?
)

fun deserializeVacancies(response: JSONObject): VacancyResponse {
    val success = response.optBoolean("success", false)
    val dataObject = response.optJSONArray("data")

    val vacancies = mutableListOf<VacancyItem>()
    if (dataObject != null) {
        for (i in 0 until dataObject.length()) {
            val vacancyObject = dataObject.optJSONObject(i)
            if (vacancyObject != null) {
                val name = vacancyObject.optString("name")
                val description = vacancyObject.optString("description")
                val teamObject = vacancyObject.optJSONObject("team")
                val team = if (teamObject != null) {
                    val id = teamObject.optInt("id")
                    val name = teamObject.optString("name")
                    TeamItem(id, name)
                } else {
                    TeamItem(0, "")
                }
                vacancies.add(VacancyItem(name, description, team))
            }
        }
    }

    return VacancyResponse(success, vacancies)
}