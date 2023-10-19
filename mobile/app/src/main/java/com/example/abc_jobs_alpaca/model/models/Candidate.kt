package com.example.abc_jobs_alpaca.model.models

import org.json.JSONObject
data class Candidate(
    val id: Int,
    val nombres: String,
    val apellidos: String,
    val email: String,
    val password: String,
    )

data class LoginCandidateRequest(
    val email: String,
    val password: String
)

//data class LoginCandidateResponse(

//)

fun deserializeCandidate(json: JSONObject): Candidate {
    val dataObject = json.optJSONObject("data")
    val candidatoObject = dataObject?.optJSONObject("candidato")

    return if (candidatoObject != null) {
        Candidate(
            id = candidatoObject.optInt("id"),
            nombres = candidatoObject.optString("nombres"),
            apellidos = candidatoObject.optString("apellidos"),
            email = candidatoObject.optString("email"),
            password = candidatoObject.optString("password"),
        )
    } else {
        Candidate(0, "", "", "", "")
    }
}


fun serializeCandidate(candidate: Candidate): JSONObject {
    var json: JSONObject = JSONObject()

    json.put("nombres", candidate.nombres)
    json.put("apellidos", candidate.apellidos)
    json.put("email", candidate.email)
    json.put("password", candidate.password)

    return json
}