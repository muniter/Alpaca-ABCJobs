package com.example.abc_jobs_alpaca.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.abc_jobs_alpaca.model.models.PersonalInfo
import com.example.abc_jobs_alpaca.model.models.PersonalInfoRequest
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.utils.MessageEvent
import com.example.abc_jobs_alpaca.utils.MessageType
import kotlinx.coroutines.launch

class PersonalInfoViewModel(
    private val token: String,
    private val abcJobsRepository: ABCJobsRepository
) : ViewModel() {

    val personalInfo: LiveData<PersonalInfo?> = liveData {

        showForm.postValue(true)
        enableForm.postValue(false)

        abcJobsRepository.getPersonalInfo(token)
            .onSuccess {
                emit(it!!.data)

                if (it?.data?.birth_date != null ||
                    it?.data?.country_code != null ||
                    it?.data?.city != null ||
                    it?.data?.address != null ||
                    it?.data?.phone != null ||
                    it?.data?.biography != null ||
                    it?.data?.languages != null
                ) {
                    showForm.postValue(true)
                }
            }
            .onFailure {
                emit(null)
            }
    }

    val showForm = MutableLiveData<Boolean>()
    val enableForm = MutableLiveData<Boolean>()

    private val messageLiveData = MutableLiveData<MessageEvent>()
    fun getMessageLiveData(): LiveData<MessageEvent> {
        return messageLiveData
    }

    private val enabledElementsLiveData = MutableLiveData<Boolean>()
    private fun setEnabledElements(state: Boolean) {
        viewModelScope.launch {
            enabledElementsLiveData.value = state
        }
    }

    fun getEnabledElementsLiveData(): LiveData<Boolean> {
        return enabledElementsLiveData
    }

    suspend fun savePersonalInfo(personalInfoRequest: PersonalInfoRequest) {
        val response = abcJobsRepository.postPersonalInfo(token, personalInfoRequest)

        response.onSuccess { it ->
            messageLiveData.postValue(MessageEvent(MessageType.SUCCESS, "Guardao"))
        }
    }

}