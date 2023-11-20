package com.example.abc_jobs_alpaca.model.models

import org.json.JSONArray
import org.json.JSONObject

data class TechnicalProofRequest (
    val idCandidate: Int,
    val result: Int
)

fun serializeTechnicalProofRequest(technicalProofRequestList: ArrayList<TechnicalProofRequest>): JSONArray {
    var technicalProofList = JSONArray()
    var technicalProofJson = JSONObject()
    technicalProofRequestList.forEach {
        technicalProofJson.put("id_candidate", it.idCandidate)
        technicalProofJson.put("result", it.result)
        technicalProofList.put(technicalProofJson)
    }
    return technicalProofList
}

fun deserializeTechnicalProofError(response: JSONObject): Exception {
    val error = response.optString("errors")
    return Exception(error)
}