package com.example.abc_jobs_alpaca.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.abc_jobs_alpaca.model.models.LoginCandidate
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginMoldel(application: Application) : AndroidViewModel(application) {
    private val abcJobsRepository = ABCJobsRepository(application)
    private val toastMessage = MutableLiveData<String>()

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    fun login() {
        val userPassword = password.value
        val userEmail = email.value

        val loginCandidate = userEmail?.takeIf { userPassword != null }?.let {
            LoginCandidate(it, userPassword!!)
        }


        viewModelScope.launch(Dispatchers.Default){
            try {
                loginCandidate?.let { abcJobsRepository.postLoginCandidate(it) }
                    ?.onSuccess { }

            } catch (e: Exception) {
                Log.d("NETWORK_ERROR", e.toString())
            }
        }
        

    }

}