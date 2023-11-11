package com.example.abc_jobs_alpaca.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abc_jobs_alpaca.model.models.ExamItem
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import kotlinx.coroutines.launch

class ExamListViewModel(private val abcJobsRepository: ABCJobsRepository): ViewModel() {
    private val tokenLiveData = MutableLiveData<String?>()
    val token = tokenLiveData
    private val _exams = MutableLiveData<List<ExamItem>?>()
    val exams: MutableLiveData<List<ExamItem>?> get() = _exams

    fun onTokenUpdated(token: String?) {
        tokenLiveData.value = token
    }

    interface NavigationListener {
        fun navigateToNextScreen()
    }

    private var navigationListener: NavigationListener? = null

    fun setNavigationListener(listener: NavigationListener) {
        navigationListener = listener
    }

    fun loadExams() {
        viewModelScope.launch {
            try {
                if (token != null) {
                    abcJobsRepository.getAllExams(token.value!!)
                        .onSuccess { response ->
                            if (response.success) {
                                _exams.postValue(response.data)
                                navigationListener?.navigateToNextScreen()
                            }
                        }
                        .onFailure {

                        }
                } else {

                }
            } catch (e: Exception) {

            }
        }
    }
}