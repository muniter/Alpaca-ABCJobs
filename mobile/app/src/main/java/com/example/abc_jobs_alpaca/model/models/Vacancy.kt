package com.example.abc_jobs_alpaca.model.models

import com.google.gson.Gson
import org.json.JSONObject

data class VacancyItem(
        val id: Int,
        val name: String,
        val open: Boolean,
        val description: String,
        val team: TeamItem,
        val preselection: List<ShortlistedCandidateItem>
)

data class VacanciesResponse(val success: Boolean, val data: List<VacancyItem>?)

data class VacancyResponse(val success: Boolean, val data: VacancyItem?)

data class Preselection(
        val id_candidate: Int,
        val id_persona: Int,
        val names: String,
        val last_names: String,
        val full_name: String,
        val email: String,
        val birth_date: String,
        val country_code: Int,
        val country: String,
        val city: String,
        val address: String,
        val phone: String,
        val biography: String,
        val languages: List<Language>,
        val result: Int
)

data class VacancySelectCandidateResponse(
        val id: Int,
        val name: String,
        val open: Boolean,
        val description: String,
        val country: Country,
        val company: CompanyDetails,
        val team: Team,
        val preselection: List<Preselection>,
        val interview_date: String
)

fun deserializeVacancySelectCandidateResponse(
        response: JSONObject
): VacancySelectCandidateResponse {
    val gson = Gson()
    return gson.fromJson(response.toString(), VacancySelectCandidateResponse::class.java)
}

fun deserializeVacancySelectCandidateError(response: JSONObject): Exception {
    val error = response.optString("errors")
    return Exception(error)
}

fun deserializeVacancies(response: JSONObject): VacanciesResponse {
    val success = response.optBoolean("success", false)
    val dataObject = response.optJSONArray("data")

    val vacancies = mutableListOf<VacancyItem>()
    if (dataObject != null) {
        for (i in 0 until dataObject.length()) {
            val vacancyObject = dataObject.optJSONObject(i)
            if (vacancyObject != null) {
                val id = vacancyObject.optInt("id")
                val name = vacancyObject.optString("name")
                val description = vacancyObject.optString("description")
                val open = vacancyObject.optBoolean("open")
                val teamObject = vacancyObject.optJSONObject("team")
                val team =
                        if (teamObject != null) {
                            val teamId = teamObject.optInt("id")
                            val teamName = teamObject.optString("name")
                            TeamItem(teamId, teamName)
                        } else {
                            TeamItem(0, "")
                        }
                /*val preselection = vacancyObject.optJSONArray("preselection")*/
                var parsedShortlistedCandidates: MutableList<ShortlistedCandidateItem> =
                        mutableListOf()
                vacancies.add(
                        VacancyItem(id, name, open, description, team, parsedShortlistedCandidates)
                )
            }
        }
    }

    return VacanciesResponse(success, vacancies)
}

fun deserializeVacancy(response: JSONObject): VacancyResponse {
    val success = response.optBoolean("success", false)
    val vacancyObject = response.optJSONObject("data")
    var vacancy: VacancyItem? = null

    if (vacancyObject != null) {
        val id = vacancyObject.optInt("id")
        val name = vacancyObject.optString("name")
        val open = vacancyObject.optBoolean("open")
        val description = vacancyObject.optString("description")
        val teamObject = vacancyObject.optJSONObject("team")
        val team =
                if (teamObject != null) {
                    val teamId = teamObject.optInt("id")
                    val teamName = teamObject.optString("name")
                    TeamItem(teamId, teamName)
                } else {
                    TeamItem(0, "")
                }
        val preselection = vacancyObject.optJSONArray("preselection")
        val parsedShortlistedCandidates: MutableList<ShortlistedCandidateItem> = mutableListOf()
        if (preselection != null) {
            for (i in 0 until preselection.length()) {
                val candidate = preselection.optJSONObject(i)
                val idCandidate = candidate.optInt("id_candidate")
                val idPersona = candidate.optInt("id_persona")
                val names = candidate.optString("names")
                val lastNames = candidate.optString("last_names")
                val fullName = candidate.optString("full_name")
                val email = candidate.optString("email")
                val birthDate = candidate.optString("birth_date")
                val countryCode = candidate.optInt("country_code")
                val country = candidate.optString("country")
                val city = candidate.optString("city")
                val address = candidate.optString("address")
                val phone = candidate.optString("phone")
                val biography = candidate.optString("biography")
                val languages = candidate.optJSONArray("languages")
                val parsedLanguages: MutableList<Language> = mutableListOf()
                if (languages != null) {
                    for (j in 0 until languages.length()) {
                        val language = languages.optJSONObject(j)
                        parsedLanguages.add(
                                Language(language.optString("id"), language.optString("name"))
                        )
                    }
                }
                val result = candidate.optInt("result")
                parsedShortlistedCandidates.add(
                        ShortlistedCandidateItem(
                                idCandidate,
                                idPersona,
                                names,
                                lastNames,
                                fullName,
                                email,
                                birthDate,
                                countryCode,
                                country,
                                city,
                                address,
                                phone,
                                biography,
                                parsedLanguages,
                                result
                        )
                )
            }
        }
        vacancy = VacancyItem(id, name, open, description, team, parsedShortlistedCandidates)
    }

    return VacancyResponse(success, vacancy)
}

fun deserializeVacancyError(response: JSONObject): Exception {
    val error = response.optString("errors")
    return Exception(error)
}
