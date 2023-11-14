package com.example.abc_jobs_alpaca.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abc_jobs_alpaca.model.models.AcademicInfoRequest
import com.example.abc_jobs_alpaca.model.models.AcademicInfoType
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository

class AcademicInfoCreateViewModel(
    private val abcJobsRepository: ABCJobsRepository
) : ViewModel() {

    private val tokenLiveData = MutableLiveData<String?>()
    val typesTitles = MutableLiveData<List<AcademicInfoType>>()
    val token = tokenLiveData
    private val years = List<Int>(25) { 2023 - it }
    private val enabledElementsLiveData = MutableLiveData<Boolean>()

    fun getEnabledElementsLiveData(): LiveData<Boolean> {
        return enabledElementsLiveData
    }

    fun onTokenUpdated(token: String?) {
        tokenLiveData.value = token
    }

    fun getYears(): List<Int> {
        return years
    }

    fun getIdTypeDegree(nameTypeDegree: String): Int {
        var idTypeDegree = 0
        for (type in typesTitles.value!!) {
            if (type.name == nameTypeDegree) {
                idTypeDegree = type.id
            }
        }
        return idTypeDegree
    }

    suspend fun getTypesDegree() {
        try {
            if (token.value != null) {
                abcJobsRepository.getTypeTitles(token.value!!)
                    .onSuccess { response ->
                        if (response.success) {
                            typesTitles.value = response.data
                        } else {
                            //TODO: message
                            //messageLiveData.postValue(MessageEvent(MessageType.ERROR, response.message))
                        }
                    }
                    .onFailure {
                        //TODO: message
                        //messageLiveData.postValue(MessageEvent(MessageType.ERROR, error.message.toString()))
                    }
            } else {
                Log.d("AcademicInfoCreateViewModel", "getTypesDegree: token is null")
            }
        } catch (e: Exception) {
            //messageLiveData.postValue(MessageEvent(MessageType.ERROR, e.message.toString()))
        }
    }

    // TODO: Block controls while we are waiting for the response if the response is not successful able the controls again
    suspend fun saveAcademicInfoItem(newAcademicInfo: AcademicInfoRequest) {
        abcJobsRepository.postAcademicInfo(token.value!!, newAcademicInfo)
            .onSuccess {
                //TODO: message
            }
            .onFailure {
                //TODO: message

            }
    }

    interface NavigationListener {
        fun navigateToNextScreen()
    }

    private var navigationListener: NavigationListener? = null

    fun setNavigationListener(listener: NavigationListener) {
        navigationListener = listener
    }
}