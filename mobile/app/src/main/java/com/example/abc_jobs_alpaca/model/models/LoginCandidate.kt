package com.example.abc_jobs_alpaca.model.models

import org.json.JSONObject

data class LoginCandidate (
    val email: String,
    val password: String,
    )

fun deserializeLoginCandidate(json: JSONObject): LoginCandidate {
    val dataObject = json.optJSONObject("data")
    val candidatoObject = dataObject?.optJSONObject("candidato")

    return if (candidatoObject != null) {
        LoginCandidate(
            email = candidatoObject.optString("email"),
            password = candidatoObject.optString("password"),
        )
    } else {
        LoginCandidate("", "")
    }
}


fun serializeLoginCandidate(candidate: LoginCandidate): JSONObject {
    var json: JSONObject = JSONObject()

    json.put("email", candidate.email)
    json.put("password", candidate.password)

    return json
}