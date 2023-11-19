package com.example.abc_jobs_alpaca.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.NetworkError
import com.example.abc_jobs_alpaca.model.models.ConfigData
import com.example.abc_jobs_alpaca.model.models.UserLoginRequest
import com.example.abc_jobs_alpaca.model.models.UserLoginResponse
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.utils.MessageEvent
import com.example.abc_jobs_alpaca.utils.MessageType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val application: Application, private val abcJobsRepository: ABCJobsRepository) : ViewModel() {
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val enabledElementsLiveData = MutableLiveData<Boolean>()
    private fun setEnabledElements(state: Boolean) {
        viewModelScope.launch {
            enabledElementsLiveData.value = state
        }
    }

    fun getEnabledElementsLiveData(): LiveData<Boolean> {
        return enabledElementsLiveData
    }

    fun interface NavigationListener {
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

    suspend fun login(userEmail: String, userPassword: String) {
        val loginCandidate = userEmail?.takeIf { userPassword != null }?.let {
            UserLoginRequest(it, userPassword!!)
        }

            try {
                loginCandidate?.let { doLogin(it) }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                setEnabledElements(true)
            }
    }

    private fun setConfigToPreferences(config: ConfigData) {
        val sharedPreferences = application.getSharedPreferences("AppPreferences", 0)
        val editor = sharedPreferences.edit()
        when (config.languageApp.name) {
            "ES" -> editor.putString("language", "es")
            "EN" -> editor.putString("language", "en")
        }
        editor.putString("dateFormat", config.dateFormat.formatString)
        editor.putString("timeFormat", config.timeFormat.formatString)
        editor.apply()

    }

    private suspend fun doLogin(loginRequest: UserLoginRequest) {
        val response = abcJobsRepository.postLoginUser(loginRequest)
        response.onSuccess { it ->
            handleSuccessResponse(it)
            val xx = abcJobsRepository.getConfig(it.data?.token!!)
            xx.onSuccess {
                setConfigToPreferences(it!!)
            }
            xx.onFailure{
            }
        }
            .onFailure { handleError(it) }
    }

    private fun handleSuccessResponse(response: UserLoginResponse) {
        if (response.success) {
            messageLiveData.postValue(response.data?.let {
                MessageEvent(MessageType.SUCCESS, it)
            })

            val user = response.data?.usuario
            if(user?.idEmpresa != null){
                saveUserFlagInSharedPreferences(true)
            }else{
                saveUserFlagInSharedPreferences(false)
            }

            val token = response.data?.token
            token?.let { saveTokenInSharedPreferences(it) }

            navigationListener?.navigateToNextScreen()
        }
    }

    private fun handleError(error: Throwable) {
        val errorMessage = when (error) {
            is NetworkError -> error.message.toString()
            is Exception -> error.message.orEmpty()
            else -> String()
        }

        messageLiveData.postValue(MessageEvent(MessageType.ERROR, errorMessage))
    }

    private fun saveTokenInSharedPreferences(token: String) {
        val sharedPreferences = application.getSharedPreferences("AppPreferences", 0)
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.apply()
    }

    private fun saveUserFlagInSharedPreferences(isCompany: Boolean) {
        val sharedPreferences = application.getSharedPreferences("AppPreferences", 0)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isCompany", isCompany)
        editor.apply()
    }


}