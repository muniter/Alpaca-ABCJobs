package com.example.abc_jobs_alpaca.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.model.models.AcademicInfoItem
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import kotlinx.coroutines.launch

class AcademicInfoViewModel (private val abcJobsRepository: ABCJobsRepository) : ViewModel() {

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

    fun loadAcademicItemsInfo() {
        viewModelScope.launch {
            try {
                if (token != null) {
                    abcJobsRepository.getAcademicInfo(token.value!!)
                        .onSuccess { response ->
                            if (response.success) {
                                Log.d("AcademicInfoViewModel", "loadAcademicItemsInfo: ${response.data}")
                                _academicInfoList.postValue(response.data)

                            }
                        }
                        .onFailure {
                            //TODO: something
                        }
                } else {
                    //TODO: message
                }
            } catch (e: Exception) {

            }
        }
    }

}