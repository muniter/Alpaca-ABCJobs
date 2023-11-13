package com.example.abc_jobs_alpaca.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abc_jobs_alpaca.model.models.ExamItem
import com.example.abc_jobs_alpaca.model.models.ExamItemExtend
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository

class ExamListViewModel(private val abcJobsRepository: ABCJobsRepository): ViewModel() {
    private val tokenLiveData = MutableLiveData<String?>()
    val token = tokenLiveData
    private val _exams = MutableLiveData<List<ExamItem>?>()
    private val _examsResult = MutableLiveData<List<ExamItemExtend>?>()
    private val _examsResultMapped = MutableLiveData<List<ExamItemExtend>?>()
    val exams: MutableLiveData<List<ExamItem>?> get() = _exams
    val examsResult: MutableLiveData<List<ExamItemExtend>?> get() = _examsResult
    val examsResultMapped: MutableLiveData<List<ExamItemExtend>?> get() = _examsResultMapped

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

    suspend fun loadExams() {
            try {
                if (token != null) {
                    abcJobsRepository.getAllExams(token.value!!)
                        .onSuccess { response ->
                            if (response.success) {
                                _exams.postValue(response.data)
                                Log.d("ExamListViewModel", "loadExams: ${response.data}")
                                //navigationListener?.navigateToNextScreen()
                            }
                        }
                        .onFailure {

                        }
                } else {

                }
            } catch (e: Exception) {

            }
    }

    suspend fun loadExamsResult() {
        try {
            if (token != null) {
                abcJobsRepository.getAllExamsResult(token.value!!)
                    .onSuccess { response ->
                        if (response.success) {
                            if (response.data != null) {
                                _examsResult.postValue(response.data)
                                Log.d("ExamListViewModel", "loadExamsResult: ${response.data}")
                                //navigationListener?.navigateToNextScreen()
                            } else {
                                Log.d("ExamListViewModel", "loadExamsResult: ${response.data}")
                            }
                        }
                    }
                    .onFailure {
                    }
            } else {
                //TODO: Manejo adicional si token es nulo
            }
        } catch (e: Exception) {
            // TODO: Manejo de excepciones
        }
    }

    fun mapExamsResult(
        exams: MutableLiveData<List<ExamItem>?>,
        examsResult: MutableLiveData<List<ExamItemExtend>?>
    ) {
        val examsResultValue = examsResult.value
        val examsValue = exams.value
        val examsResultMapped = mutableListOf<ExamItemExtend>()
        if (examsResult.value != null && exams.value != null) {
            for (exam in examsValue!!) {
                for (examResult in  examsResultValue!!) {
                    if (exam.id == examResult.exam.id ) {
                        examsResultMapped.add(
                            ExamItemExtend(
                                examResult.id,
                                exam,
                                examResult.id_candidato,
                                examResult.result,
                                examResult.completed
                            )
                        )
                    }
                }
                if(!examsResultMapped.any { it.exam.id == exam.id })
                    examsResultMapped.add(ExamItemExtend(0, exam, 0, 0, false))
            }
            _examsResultMapped.postValue(examsResultMapped)
        }
        else if( examsResult.value == null && exams.value != null){
            for (exam in examsValue!!) {
                examsResultMapped.add(ExamItemExtend(0, exam, 0, 0, false))
            }
            _examsResultMapped.postValue(examsResultMapped)
        }
        else {
            Log.d("ExamListViewModel", "mapExamsResult: else")
        }
    }
}