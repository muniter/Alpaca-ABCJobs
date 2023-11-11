package com.example.abc_jobs_alpaca.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoRequest
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoType
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository

class TechnicalInfoCreateViewModel(
    private val abcJobsRepository: ABCJobsRepository
) : ViewModel() {
    private val tokenLiveData = MutableLiveData<String?>()
    val typesTechnicalInfoTypes = MutableLiveData<List<TechnicalInfoType>?>()
    val token = tokenLiveData
    private val enabledElementsLiveData = MutableLiveData<Boolean>()

    interface NavigationListener {
        fun navigateToNextScreen()
    }

    private var navigationListener: NavigationListener? = null

    fun setNavigationListener(listener: NavigationListener) {
        navigationListener = listener
    }

    fun getEnabledElementsLiveData(): LiveData<Boolean> {
        return enabledElementsLiveData
    }

    fun onTokenUpdated(token: String?) {
        tokenLiveData.value = token
    }

    suspend fun getTypesTechnicalItems() {
        try {
            if (token != null) {
                abcJobsRepository.getTechnicalInfoTypes(token.value!!)
                    .onSuccess { response ->
                        if (response.success) {
                            typesTechnicalInfoTypes.value = response.data
                        } else {
                            //TODO: something
                        }
                    }
                    .onFailure {
                        //TODO: something
                    }
            }
        } catch (e: Exception) {
            //TODO: something
        }
    }

    fun getIdTypeTechnicalItem(nameTypeTechnicalItem: String): Int {
        var idTypeTechnicalItem = 0
        for (type in typesTechnicalInfoTypes.value!!) {
            if (type.name == nameTypeTechnicalItem) {
                idTypeTechnicalItem = type.id
            }
        }
        return idTypeTechnicalItem
    }

    suspend fun saveTechnicalInfoItem(newTechnicalInfo: TechnicalInfoRequest) {
        try {
            if (token != null) {
                Log.d(
                    "TechnicalInfoCreateViewModel",
                    "saveTechnicalInfoItem: ${newTechnicalInfo}"
                )
                abcJobsRepository.postTechnicalInfo(token.value!!, newTechnicalInfo)
                    .onSuccess { }
                    .onFailure { }
            }
        } catch (e: Exception) {

        }
    }
}