package com.example.abc_jobs_alpaca.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.abc_jobs_alpaca.model.models.Candidate
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CandidateRegisterModel(application: Application) : AndroidViewModel(application) {
    private val abcJobsRepository=  ABCJobsRepository(application);

    fun postCandidate(newCandidate: Candidate) {
        try {
            viewModelScope.launch(Dispatchers.Default) {
                withContext(Dispatchers.IO) {
                    val data = abcJobsRepository.postCandidate(newCandidate)
                }
            }
        } catch (e: Exception) {
            Log.d("NETWORK_ERROR", e.toString())
        }
    }
}