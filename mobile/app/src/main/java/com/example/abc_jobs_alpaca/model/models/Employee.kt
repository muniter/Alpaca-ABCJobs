package com.example.abc_jobs_alpaca.model.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import java.util.Date

data class Employee(
    val id: Int,
    @SerializedName("id_persona") val idPersona: Int,
    val name: String,
    val title: String,
    val company: CompanyDetails,
    val personality: Personality,
    val skills: List<SkillInfoType>,
    val evaluations: List<Evaluation>,
    val teams: List<Team>
)

data class EmployeePerformance(
    val id: Int,
    val fullName: String,
    val result: Int
)

data class EvaluationEmployeeRequest(
    val date: String,
    val result: Int
)

data class EmployeesResponse(
    val success: Boolean,
    val data: List<Employee>?)

data class EmployeeResponse(
    val success: Boolean,
    val data: Employee
)

fun deserializeEmployeesResponse(response: JSONObject): EmployeesResponse {
    val gson = Gson()
    return gson.fromJson(response.toString(), EmployeesResponse::class.java)
}

fun deserializeEmployeesError(response: JSONObject): Exception {
    val error = response.optString("errors")
    return Exception(error)
}

fun serializeEvaluationEmployeeRequest(requestBody: EvaluationEmployeeRequest): JSONObject{
    val jsonObject = JSONObject()
    jsonObject.put("date", requestBody.date)
    jsonObject.put("result", requestBody.result)
    return jsonObject
}

fun deserializeEmployeeResponse(response: JSONObject): EmployeeResponse{
    val gson = Gson()
    return gson.fromJson(response.toString(), EmployeeResponse::class.java)
}

fun deserializeEmployeeError(error: JSONObject): Exception{
    val error = error.optString("errors")
    return Exception(error)
}