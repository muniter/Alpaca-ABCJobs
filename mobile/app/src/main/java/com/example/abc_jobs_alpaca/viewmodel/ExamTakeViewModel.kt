package com.example.abc_jobs_alpaca.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abc_jobs_alpaca.model.models.Answer
import com.example.abc_jobs_alpaca.model.models.Question
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import kotlinx.coroutines.launch
import org.json.JSONArray

class ExamTakeViewModel(
    private val abcJobsRepository: ABCJobsRepository
): ViewModel() {

    private val tokenLiveData = MutableLiveData<String?>()
    val token = tokenLiveData
    val question = MutableLiveData<Question>()

    fun onTokenUpdated(token: String?) {tokenLiveData.value = token}

    fun postStartExam(idExam: Int) {
        viewModelScope.launch {
            try {
                if (token != null) {
                    abcJobsRepository.postExamStart(token.value!!, idExam)
                        .onSuccess { response ->
                            if (response.success) {
                                question.postValue(response.data.next_question)
                                Log.d("ExamTakeViewModel", "postStartExam: ${response.data.next_question}")
                            } else {
                                Log.d("ExamTakeViewModel", "postStartExam: ${response.success}")

                            }
                        }
                        .onFailure {
                            Log.d("ExamTakeViewModel", "postStartExam: ${it.message}")
                        }
                }
            } catch (e: Exception) {

            }
        }
    }

}