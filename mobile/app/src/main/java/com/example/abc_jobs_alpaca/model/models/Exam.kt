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

data class ExamItemExtend(
    val id: Int,
    val exam: ExamItem,
    val id_candidato: Int,
    val result: Int,
    val completed: Boolean
)

data class ExamsResponse(
    val success: Boolean,
    val data: List<ExamItem>?
)


data class ExamsExtendResponse(
    val success: Boolean,
    val data: List<ExamItemExtend>?
)

data class ExamStartResponse(
    val success: Boolean,
    val data: ExamStartData
)

data class AnswerQuestionResponse(
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

fun deserializeExams(response: JSONObject): ExamsResponse {
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

    return ExamsResponse(success, exams)
}


fun deserializeExamsResult(response: JSONObject): ExamsExtendResponse {
    val success = response.optBoolean("success", false)
    val dataObject = response.optJSONArray("data")

    val exams = mutableListOf<ExamItemExtend>()
    if (dataObject != null) {
        for (i in 0 until dataObject.length()) {
            val examObject = dataObject.optJSONObject(i)
            if (examObject != null) {
                val id = examObject.optInt("id")
                val id_candidato = examObject.optInt("id_candidato")
                val result = examObject.optInt("result")
                val completed = examObject.optBoolean("completed")
                val exam = examObject.optJSONObject("exam")
                val id_exam = exam?.optInt("id")
                val skill = exam?.optJSONObject("skill")
                val id_skill = skill?.optInt("id")
                val name_skill = skill?.optString("name")
                val skillInfoType = SkillInfoType(id_skill!!, name_skill!!)
                val number_of_questions = exam?.optInt("number_of_questions")
                val examItem = ExamItem(id_exam!!, skillInfoType, completed, number_of_questions!!)
                val examItemExtend = ExamItemExtend(id, examItem, id_candidato, result, completed)
                exams.add(examItemExtend)
            }
        }
    }
    return ExamsExtendResponse(success, exams)
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

fun deserializeAnswerQuestion(response: JSONObject): AnswerQuestionResponse {
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

    return AnswerQuestionResponse(success, examStartData)
}


fun serializeAnswer(answer: Answer): JSONObject {
    val answerJson = JSONObject()
    answerJson.put("id", answer.id)
    answerJson.put("id_question", answer.id_question)
    answerJson.put("answer", answer.answer)
    return answerJson
}

fun deserializerAnswers(response: JSONArray): List<Answer> {
    val answers = mutableListOf<Answer>()
    for (i in 0 until response.length()) {
        val answerObject = response.optJSONObject(i)
        if (answerObject != null) {
            val id = answerObject.optInt("id")
            val id_question = answerObject.optInt("id_question")
            val answer = answerObject.optString("answer")
            answers.add(Answer(id, id_question, answer))
        }
    }
    return answers
}

fun deserializeAnswerQuestionError(response: JSONObject): Exception {
    val error = response.optString("errors")
    return Exception(error)
}


fun deserializeExamStartError(response: JSONObject): Exception {
    val error = response.optString("errors")
    return Exception(error)
}