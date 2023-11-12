package com.example.abc_jobs_alpaca.model.models

import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

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

data class ExamStartResponse(
    val success: Boolean,
    val data: ExamStartData
)

data class ExamStartData(
    val id_result: Int,
    val id_exam: Int,
    val next_question: Question,
    val result: Any?
)

data class Question(
    val id: Int,
    val id_exam: Int,
    val question: String,
    val difficulty: Int,
    val answers: JSONArray?
)

data class Answer(
    val id: Int,
    val id_question: Int,
    val answer: String
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

fun deserializeExamStart(response: JSONObject): ExamStartResponse {
    val success = response.optBoolean("success", false)
    val dataObject = response.optJSONObject("data")

    val id_result = dataObject?.optInt("id_result")
    val id_exam = dataObject?.optInt("id_exam")
    val next_question = dataObject?.optJSONObject("next_question")
    val result = dataObject?.opt("result")

    val question = Question(
        next_question?.optInt("id")!!,
        next_question.optInt("id_exam"),
        next_question.optString("question"),
        next_question.optInt("difficulty"),
        next_question.optJSONArray("answers")
    )

    val examStartData = ExamStartData(id_result!!, id_exam!!, question!!, result!!)

    return ExamStartResponse(success, examStartData)
}

fun deserializeAnswer(response: JSONObject): Answer {
    val id = response.optInt("id")
    val id_question = response.optInt("id_question")
    val answer = response.optString("answer")

    return Answer(id, id_question, answer)
}

fun deserializeQuestion(response: JSONObject): Question {
    val id = response.optInt("id")
    val id_exam = response.optInt("id_exam")
    val question = response.optString("question")
    val difficulty = response.optInt("difficulty")
    val answers = response.optJSONArray("answers")

    return Question(id, id_exam, question, difficulty, answers)
}


fun deserializeExamStartError(response: JSONObject): Exception {
    val error = response.optString("error")
    return Exception(error)
}