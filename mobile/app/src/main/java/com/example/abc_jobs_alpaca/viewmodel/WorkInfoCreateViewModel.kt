package com.example.abc_jobs_alpaca.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abc_jobs_alpaca.model.models.SkillInfoType
import com.example.abc_jobs_alpaca.model.models.WorkInfoRequest
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository

class WorkInfoCreateViewModel(
    private val abcJobsRepository: ABCJobsRepository
) : ViewModel() {

    private val tokenLiveData = MutableLiveData<String?>()
    val typesSkills = MutableLiveData<List<SkillInfoType>?>()
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

    fun getIdTypeSkill(nameTypeSkill: String): Int {
        var idTypeSkill = 0
        for (type in typesSkills.value!!) {
            if (type.name == nameTypeSkill) {
                idTypeSkill = type.id
            }
        }
        return idTypeSkill
    }

    suspend fun getTypesSkills() {
        try {
            if (token != null) {
                abcJobsRepository.getTypesSkill(token.value!!)
                    .onSuccess { response ->
                        if (response.success) {
                            typesSkills.value = response.data
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
                Log.d("WorkInfoCreateViewModel", "getTypesDegree: token is null")
            }
        } catch (e: Exception) {
            //messageLiveData.postValue(MessageEvent(MessageType.ERROR, e.message.toString()))
        }

    }

    // TODO: Block controls while we are waiting for the response if the response is not successful able the controls again
    suspend fun saveWorkInfoItem(newWorkInfo: WorkInfoRequest) {
        abcJobsRepository.postWorkInfo(token.value!!, newWorkInfo)
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