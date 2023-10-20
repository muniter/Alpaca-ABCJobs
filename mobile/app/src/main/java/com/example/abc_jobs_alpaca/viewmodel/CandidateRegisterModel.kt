package com.example.abc_jobs_alpaca.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.abc_jobs_alpaca.model.models.Candidate
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.android.volley.NetworkError
import com.example.abc_jobs_alpaca.R


class CandidateRegisterModel(application: Application) : AndroidViewModel(application) {
    private val abcJobsRepository = ABCJobsRepository(application)
    private val toastMessage = MutableLiveData<String>()


    fun postCandidate(newCandidate: Candidate) {
        // Disable UI elements here

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result = abcJobsRepository.postCandidate(newCandidate)
                result.onSuccess { candidate ->
                    // Request was successful. Handle it accordingly.
                    showToastMessage("Registration was successful")

                }
                result.onFailure { error ->
                    // Request was not successful. Handle it accordingly.
                    when (error) {
                        is NetworkError -> {
                            showToastMessage("Network error: ${error.message}")
                        }
                        else -> {
                            showToastMessage("Request failed: ${error.message}")
                        }
                    }

                }
            } catch (e: Exception) {
                Log.d("NETWORK_ERROR", e.toString())
                showToastMessage("Network error: ${e.message}")
            }
        }
    }


    fun showToastMessage(message: String) {
        toastMessage.postValue(message)
    }
    fun getToastMessage(): LiveData<String> {
        return toastMessage
    }

}



