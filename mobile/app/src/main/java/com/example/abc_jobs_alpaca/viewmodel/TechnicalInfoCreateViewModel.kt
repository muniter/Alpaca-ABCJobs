package com.example.abc_jobs_alpaca.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoItem
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoRequest
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoType
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import kotlinx.coroutines.launch

class TechnicalInfoCreateViewModel(
    private val abcJobsRepository: ABCJobsRepository
): ViewModel() {
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
    fun onTokenUpdated(token: String?) {tokenLiveData.value = token}

    fun getTypesTechnicalItems() {
        viewModelScope.launch {
            try {
                if (token != null) {
                    abcJobsRepository.getTechnicalInfoTypes(token.value!!)
                        .onSuccess { response ->
                            if (response.success) {
                                typesTechnicalInfoTypes.postValue(response.data)
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
    }

    fun saveTechnicalInfoItem(newTechnicalInfo: TechnicalInfoRequest) {
        viewModelScope.launch {
            try {
                if (token != null) {
                    abcJobsRepository.postTechnicalInfo(token.value!!, newTechnicalInfo)
                        .onSuccess { }
                        .onFailure { }
                }
            }
            catch (e: Exception) {}
        }
    }
}