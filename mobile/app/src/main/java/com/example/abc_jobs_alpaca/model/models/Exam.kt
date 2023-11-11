package com.example.abc_jobs_alpaca.model.models

import org.json.JSONObject

data class ExamItem(
    val id: Int,
    val skill: SkillInfoType?,
    val completed: Boolean,
    val number_of_questions: Int
)

data class ExamItemResponse(
    val success: Boolean,
    val data: List<ExamItem>?
)


fun deserializeExamItems(response: JSONObject): ExamItemResponse {
    val success = response.optBoolean("success", false)
    val dataObject = response.optJSONArray("data")

    val exams = mutableListOf<ExamItem>()
    if (dataObject != null) {
        for (i in 0 until dataObject.length()) {
            val examObject = dataObject.optJSONObject(i)
            if (examObject != null) {
                val id = examObject.optInt("id")
                val skill = examObject.optJSONObject("skill")
                val skillId = skill?.optInt("id")
                val skillName = skill?.optString("name")
                val skillInfoType = skillId?.let { skillName?.let { it1 -> SkillInfoType(it, it1) }
                }
                val completed = examObject.optBoolean("completed")
                val number_of_questions = examObject.optInt("number_of_questions")
                exams.add(ExamItem(id, skillInfoType, completed, number_of_questions))
            }
        }
    }

    return ExamItemResponse(success, exams)
}