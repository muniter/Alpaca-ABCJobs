package com.example.abc_jobs_alpaca.model.models

import org.json.JSONObject

data class InterviewsResponse(
    val success: Boolean,
    val data: List<InterviewItem>
)

data class InterviewItem(
    val id_vacancy: Int,
    val name: String,
    val company: String,
    val interview_date: String,
    val completed: Boolean,
    val result: Any? = null
)


fun deserializeInterviews(json: JSONObject): InterviewsResponse {
    val success = json.optBoolean("success", false)
    val dataArray = json.optJSONArray("data")

    val data = ArrayList<InterviewItem>()

    if (dataArray != null) {
        for (i in 0 until dataArray.length()) {
            val dataObject = dataArray.optJSONObject(i)
            val id_vacancy = dataObject?.optInt("id_vacancy") ?: 0
            val name = dataObject?.optString("name") ?: ""
            val company = dataObject?.optString("company") ?: ""
            val interview_date = dataObject?.optString("interview_date") ?: ""
            val completed = dataObject?.optBoolean("completed") ?: false
            val result = dataObject?.opt("result")
            val vacancyItem = InterviewItem(id_vacancy, name, company, interview_date, completed, result)
            data.add(vacancyItem)
        }
    }

    return InterviewsResponse(success, data)
}