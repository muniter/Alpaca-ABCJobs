package com.example.abc_jobs_alpaca.model.models

import org.json.JSONObject

data class UserLoginRequest(
    val email: String,
    val password: String
)

data class UserLoginResponse(
    val success: Boolean,
    val data: UserData?
)

data class UserData(
    val usuario: User,
    val token: String?
)

data class User(
    val id: Int,
    val email: String,
    val id_candidato: Int
)

fun deserializeLoginCandidate(json: JSONObject): UserLoginResponse {
    val success = json.optBoolean("success", false)
    val dataObject = json.optJSONObject("data")
    val usuarioObject = dataObject?.optJSONObject("usuario")
    val token = dataObject?.optString("token", "")

    val userDataResponse = if (usuarioObject != null) {
        val id = usuarioObject.optInt("id")
        val email = usuarioObject.optString("email")
        val idCandidato = usuarioObject.optInt("id_candidato")

        val user = User(id, email, idCandidato)
        UserData(user, token)
    } else {
        UserData(User(0, "", 0), token)
    }

    return UserLoginResponse(success, userDataResponse)
}



fun serializeLoginUser(candidate: UserLoginRequest): JSONObject {
    var json: JSONObject = JSONObject()

    json.put("email", candidate.email)
    json.put("password", candidate.password)

    return json
}