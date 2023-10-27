package com.example.abc_jobs_alpaca.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

class LoginMoldel(application: Application) : AndroidViewModel(application) {
    private val abcJobsRepository = ABCJobsRepository(application)
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

    fun login() {
        val userPassword = password.value
        val userEmail = email.value

        val loginCandidate = userEmail?.takeIf { userPassword != null }?.let {
            UserLoginRequest(it, userPassword!!)
        }

        viewModelScope.launch(Dispatchers.Default) {
            try {
                loginCandidate?.let { doLogin(it) }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                setEnabledElements(true)
            }
        }
    }

    private fun setConfigToPreferences(config: ConfigData) {
        val sharedPreferences = getApplication<Application>()
            .getSharedPreferences("AppPreferences", 0)
        val editor = sharedPreferences.edit()
        editor.putString("language", config.languageApp.name)
        editor.putString("dateFormat", config.dateFormat.formatString)
        editor.putString("timeFormat", config.timeFormat.formatString)
        editor.apply()

        // Log for test
        Log.d("LoginViewModel", "Language: ${config.languageApp}")
        // from preferences
        val language = sharedPreferences.getString("language", "en")
        Log.d("LoginViewModel", "Language from preferences: $language")
        val dateFormat = sharedPreferences.getString("dateFormat", "DD/MM/YYYY")
        Log.d("LoginViewModel", "Date format from preferences: $dateFormat")
        val timeFormat = sharedPreferences.getString("timeFormat", "24 horas")
        Log.d("LoginViewModel", "Time format from preferences: $timeFormat")
    }

    private suspend fun doLogin(loginRequest: UserLoginRequest) {
        val response = abcJobsRepository.postLoginUser(loginRequest)
        response.onSuccess { it ->
            handleSuccessResponse(it)
            val xx = abcJobsRepository.getConfig(it.data?.token!!)
            Log.d("LoginViewModel", "Token: ${it.data?.token}")
            xx.onSuccess {
                setConfigToPreferences(it!!)
            }
            xx.onFailure{
                Log.d("LoginViewModel", "Error get config: ${it}")
            }
        }
            .onFailure { handleError(it) }
    }

    private fun handleSuccessResponse(response: UserLoginResponse) {
        if (response.success) {
            messageLiveData.postValue(response.data?.let {
                MessageEvent(MessageType.SUCCESS, it)
            })

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
        val sharedPreferences = getApplication<Application>()
                                .getSharedPreferences("AppPreferences", 0)
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.apply()
    }


}