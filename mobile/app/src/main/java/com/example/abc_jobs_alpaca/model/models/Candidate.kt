package com.example.abc_jobs_alpaca.model.models

import org.json.JSONObject

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

    val candidato = candidatoObject?.let {
        val id = it.optInt("id")
        val nombres = it.optString("nombres")
        val apellidos = it.optString("apellidos")
        val email = it.optString("email")
        CandidatoData(id, nombres, apellidos, email)
    } ?: CandidatoData(0, "", "", "")

    val userDataResponse = UserDataResponse(candidato, token)
    return UserRegisterResponse(success, userDataResponse)
}

fun deserializeCandidateError(response: JSONObject): Exception {
    val success = response.optBoolean("success", false)

    if (!success) {
        val errorsObject = response.optJSONObject("errors")
        if (errorsObject != null) {
            val emailError = errorsObject.optString("email")
            if (emailError.isNotBlank()) {
                return Exception(emailError)
            }
        }
    }

    return Exception("Error en la solicitud")
}

fun serializeCandidate(candidate: UserRegisterRequest): JSONObject {
    val json = JSONObject()

    json.put("nombres", candidate.nombres)
    json.put("apellidos", candidate.apellidos)
    json.put("email", candidate.email)
    json.put("password", candidate.password)

    return json
}