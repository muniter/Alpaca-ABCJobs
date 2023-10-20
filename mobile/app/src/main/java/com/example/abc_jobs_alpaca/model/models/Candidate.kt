package com.example.abc_jobs_alpaca.model.models

import org.json.JSONObject

data class LoginCandidateRequest(
    val email: String,
    val password: String
)

data class UserRegisterResponse(
    val success: Boolean,
    val data: UserDataResponse
)

data class UserDataResponse(
    val candidato: CandidatoData,
    val token: String
)

data class CandidatoData(
    val id: Int,
    val nombres: String,
    val apellidos: String,
    val email: String
)

data class UserRegisterRequest(
    val nombres: String,
    val apellidos: String,
    val email: String,
    val password: String
)



fun deserializeCandidate(json: JSONObject): UserRegisterResponse {
    val success = json.optBoolean("success", false)
    val dataObject = json.optJSONObject("data")
    val candidatoObject = dataObject?.optJSONObject("candidato")

    val token = dataObject?.optString("token") ?: ""

    if (candidatoObject != null) {
        val id = candidatoObject.optInt("id")
        val nombres = candidatoObject.optString("nombres")
        val apellidos = candidatoObject.optString("apellidos")
        val email = candidatoObject.optString("email")

        val candidato = CandidatoData(id, nombres, apellidos, email)
        val userDataResponse = UserDataResponse(candidato, token)

        return UserRegisterResponse(success, userDataResponse)
    } else {
        return UserRegisterResponse(false, UserDataResponse(CandidatoData(0, "", "", ""), token))
    }
}


fun serializeCandidate(candidate: UserRegisterRequest): JSONObject {
    var json: JSONObject = JSONObject()

    json.put("nombres", candidate.nombres)
    json.put("apellidos", candidate.apellidos)
    json.put("email", candidate.email)
    json.put("password", candidate.password)

    return json
}