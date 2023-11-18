package com.example.abc_jobs_alpaca.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abc_jobs_alpaca.model.models.InterviewItem
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository

class InterviewViewModel( private val abcJobsRepository: ABCJobsRepository): ViewModel() {
    private val tokenLiveData = MutableLiveData<String?>()
    val token = tokenLiveData
    private val _interviewsInfoList = MutableLiveData<List<InterviewItem>?>()
    val interviewsInfoList: MutableLiveData<List<InterviewItem>?> get() = _interviewsInfoList

    fun onTokenUpdated(token: String?) {
        tokenLiveData.value = token
    }

    suspend fun loadInterviewsItemsInfo() {
        try {
            if (token?.value != null) {
                abcJobsRepository.getAllInterviews(token.value!!)
                    .onSuccess { response ->
                        if (response.success) {

                            _interviewsInfoList.value = response.data
                        }
                    }
                    .onFailure {
                    }
            }
        } catch (e: Exception) {
        }
    }
}