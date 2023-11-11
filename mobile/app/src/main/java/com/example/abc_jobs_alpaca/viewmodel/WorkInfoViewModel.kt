package com.example.abc_jobs_alpaca.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abc_jobs_alpaca.model.models.WorkInfoItem
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository

class WorkInfoViewModel(private val abcJobsRepository: ABCJobsRepository) : ViewModel() {
    private val tokenLiveData = MutableLiveData<String?>()
    val token = tokenLiveData
    private val _workInfoList = MutableLiveData<List<WorkInfoItem>?>()
    val workInfoList: MutableLiveData<List<WorkInfoItem>?> get() = _workInfoList

    private val _text = MutableLiveData<String>().apply {
        value = "This is work info Fragment"
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

    suspend fun loadWorkItemsInfo() {
        try {
            if (token != null) {
                abcJobsRepository.getWorkInfo(token.value!!)
                    .onSuccess { response ->
                        Log.d("WorkInfoViewModel", "loadWorkItemsInfo: $response")
                        if (response.success) {
                            _workInfoList.value = response.data
                            navigationListener?.navigateToNextScreen()
                        }
                    }
                    .onFailure {
                        Log.d("WorkInfoViewModel", "loadWorkItemsInfo: $it")
                    }
            } else {
                //TODO: message
            }
        } catch (e: Exception) {

        }

    }
}