package com.example.abc_jobs_alpaca.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.android.volley.NetworkError
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.model.models.UserRegisterRequest


class CandidateRegisterModel(application: Application) : AndroidViewModel(application) {
    private val abcJobsRepository = ABCJobsRepository(application)
    private val toastMessage = MutableLiveData<String>()
    private val toastError = MutableLiveData<String>()

    private val enabledElementsLiveData = MutableLiveData<Boolean>()
    fun setEnabledElements(state: Boolean) {
        // Use viewModelScope to update LiveData on the main thread
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

    fun postCandidate(newCandidate: UserRegisterRequest) {

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result = abcJobsRepository.postCandidate(newCandidate)
                result.onSuccess { response ->
                    if (response.success) {
                        showToastMessage("Cuenta creada exitosamente")
                        navigationListener?.navigateToNextScreen()
                    }
                }
                result.onFailure { error ->
                    if (error is NetworkError) {
                        showToastError("Error de red: ${error.message}")
                    } else if (error is Exception) {
                        // Verificar si el error contiene un mensaje específico del servidor
                        val serverMessage = error.message
                        if (serverMessage != null && serverMessage.isNotBlank()) {
                            showToastError("No se logró completar el registro " + serverMessage)
                        } else {
                            showToastError("Error en la solicitud")
                        }
                    } else {
                        showToastError("Error en la solicitud")
                    }
                    setEnabledElements(true)
                }

            } catch (e: Exception) {
                Log.d("NETWORK_ERROR", e.toString())
                showToastError("Error de red: ${e.message}")
            }
            setEnabledElements(true)
        }
    }

    fun showToastMessage(message: String) {
        toastMessage.postValue(message)
    }

    fun showToastError(message: String) {
        toastError.postValue(message)
    }

    fun getToastMessage(): LiveData<String> {
        return toastMessage
    }

    fun getToastError(): LiveData<String> {
        return toastError
    }
    
}



