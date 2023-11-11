package com.example.abc_jobs_alpaca.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoItem
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import kotlinx.coroutines.launch

class TechnicalInfoViewModel (private val abcJobsRepository: ABCJobsRepository) : ViewModel() {
    private val tokenLiveData = MutableLiveData<String?>()
    val token = tokenLiveData
    private val _technicalInfoList = MutableLiveData<List<TechnicalInfoItem>?>()
    val technicalInfoList: MutableLiveData<List<TechnicalInfoItem>?> get() = _technicalInfoList

    private val _text = MutableLiveData<String>().apply {
        value = "This is academic info Fragment"
    }
    val text: LiveData<String> = _text
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

    suspend fun loadTechnicalItemsInfo() {
            try {
                if (token?.value != null) {
                    abcJobsRepository.getTechnicalInfo(token.value!!)
                        .onSuccess { response ->
                            Log.d("TechnicalInfoViewModel", "loadTechnicalItemsInfo: $response")
                            if (response.success) {
                                _technicalInfoList.value = response.data
                                navigationListener?.navigateToNextScreen()
                            }
                        }
                        .onFailure {
                            Log.d("TechnicalInfoViewModel", "loadTechnicalItemsInfo: $it")
                        }
                } else {
                    //TODO: message
                }
            } catch (e: Exception) {

            }

    }
}