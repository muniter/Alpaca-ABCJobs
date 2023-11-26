package com.example.abc_jobs_alpaca.model.models

import com.google.gson.Gson
import org.json.JSONObject

data class Team(
        val id: Int,
        val name: String,
        val company: CompanyDetails,
        val employees: List<Employee>
)

data class TeamsResponse(val success: Boolean, val data: List<Team>?)

fun deserializeTeams(response: JSONObject): TeamsResponse {
    val gson = Gson()
    return gson.fromJson(response.toString(), TeamsResponse::class.java)
}

data class TeamItem(val id: Int, val name: String)
