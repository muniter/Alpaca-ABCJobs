package com.example.abc_jobs_alpaca.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abc_jobs_alpaca.model.models.AcademicInfoItem
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository

class AcademicInfoViewModel(private val abcJobsRepository: ABCJobsRepository) : ViewModel() {

    private val tokenLiveData = MutableLiveData<String?>()
    val token = tokenLiveData
    private val _academicInfoList = MutableLiveData<List<AcademicInfoItem>?>()
    val academicInfoList: MutableLiveData<List<AcademicInfoItem>?> get() = _academicInfoList

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

    suspend fun loadAcademicItemsInfo() {
        try {
            if (token?.value != null) {
                abcJobsRepository.getAcademicInfo(token.value!!)
                    .onSuccess { response ->
                        if (response.success) {
                            _academicInfoList.value = response.data
                        }
                    }
                    .onFailure {
                        _academicInfoList.value = null
                    }
            } else {
                //TODO: message
            }
        } catch (e: Exception) {

        }

    }

}