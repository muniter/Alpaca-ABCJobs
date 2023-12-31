package com.example.abc_jobs_alpaca.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.NetworkError
import com.example.abc_jobs_alpaca.model.models.UserRegisterRequest
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.utils.MessageEvent
import com.example.abc_jobs_alpaca.utils.MessageType
import kotlinx.coroutines.launch

class CandidateRegisterViewModel(private val abcJobsRepository: ABCJobsRepository) : ViewModel() {

    private val enabledElementsLiveData = MutableLiveData<Boolean>()
    fun setEnabledElements(state: Boolean) {
        viewModelScope.launch {
            enabledElementsLiveData.value = state
        }
    }

    fun getEnabledElementsLiveData(): LiveData<Boolean> {
        return enabledElementsLiveData
    }

    interface NavigationListener {
        fun navigateToNextScreen()
    }

    private var navigationListener: NavigationListener? = null

    fun setNavigationListener(listener: NavigationListener) {
        navigationListener = listener
    }

    private val messageLiveData = MutableLiveData<MessageEvent>()
    fun getMessageLiveData(): LiveData<MessageEvent> {
        return messageLiveData
    }

    suspend fun postCandidate(newCandidate: UserRegisterRequest) {

        try {
            val result = abcJobsRepository.postCandidate(newCandidate)
            result.onSuccess { response ->
                if (response.success) {
                    messageLiveData.postValue(MessageEvent(MessageType.SUCCESS, response.data))
                    navigationListener?.navigateToNextScreen()
                }
            }

            result.onFailure { error ->
                if (error is NetworkError) {
                    messageLiveData.postValue(
                        MessageEvent(
                            MessageType.ERROR,
                            error.message.toString()
                        )
                    )
                } else if (error is Exception) {
                    val serverMessage = error.message
                    if (!serverMessage.isNullOrBlank()) {
                        messageLiveData.postValue(MessageEvent(MessageType.ERROR, serverMessage))
                    } else {
                        messageLiveData.postValue(MessageEvent(MessageType.ERROR, ""))
                    }
                }
                setEnabledElements(true)
            }
        } catch (e: Exception) {
            messageLiveData.postValue(MessageEvent(MessageType.ERROR, e.toString()))
        }
        setEnabledElements(true)

    }
}
