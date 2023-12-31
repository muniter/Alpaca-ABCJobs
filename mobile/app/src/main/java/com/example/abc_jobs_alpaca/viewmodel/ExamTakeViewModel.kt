package com.example.abc_jobs_alpaca.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abc_jobs_alpaca.model.models.Answer
import com.example.abc_jobs_alpaca.model.models.Question
import com.example.abc_jobs_alpaca.model.models.deserializerAnswers
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository


class ExamTakeViewModel(
    private val abcJobsRepository: ABCJobsRepository
) : ViewModel() {

    private val tokenLiveData = MutableLiveData<String?>()
    val token = tokenLiveData
    val question = MutableLiveData<Question?>()
    val answers = MutableLiveData<List<Answer>?>()
    val idResult = MutableLiveData<Int>()

    fun onTokenUpdated(token: String?) {
        tokenLiveData.value = token
    }

    suspend fun postStartExam(idExam: Int) {
        try {
            if (token != null) {
                abcJobsRepository.postExamStart(token.value!!, idExam)
                    .onSuccess { response ->
                        if (response.success) {
                            question.postValue(response.data.next_question)
                            idResult.postValue(response.data.id_result)
                            answers.postValue(response.data.next_question?.answers?.let {
                                deserializerAnswers(
                                    it
                                )
                            })
                        } else {
                            Log.d("ExamTakeViewModel", "postStartExam2: ${response.success}")

                        }
                    }
                    .onFailure {
                        Log.d("ExamTakeViewModel", "postStartExam3: ${it.message}")
                    }
            }
        } catch (e: Exception) {
            //TODO: Manejo de exceptions
        }

    }

    suspend fun submitAnswer(idAnswer: Int) {
        try {
            if (token != null) {
                val answer = answers.value?.find { it.id == idAnswer }
                if (answer != null) {
                    abcJobsRepository.postAnswerQuestion(token.value!!, idResult.value!!, answer)
                        .onSuccess { response ->
                            if (response.success) {
                                question.value = (response.data.next_question)
                                answers.value = (response.data.next_question?.answers?.let {
                                    deserializerAnswers(
                                        it
                                    )
                                })
                            } else {
                                Log.d("ExamTakeViewModel", "postStartExam2: ${response.success}")

                            }
                        }
                        .onFailure {
                            Log.d("ExamTakeViewModel", "postStartExam3: ${it.message}")
                        }
                }
            }
        } catch (e: Exception) {
            // TODO: Manejo de excepciones
        }

    }

}


