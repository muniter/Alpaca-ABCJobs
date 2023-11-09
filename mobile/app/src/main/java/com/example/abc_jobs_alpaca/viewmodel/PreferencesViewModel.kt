package com.example.abc_jobs_alpaca.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abc_jobs_alpaca.model.models.ConfigRequest
import com.example.abc_jobs_alpaca.model.models.UserDateFormat
import com.example.abc_jobs_alpaca.model.models.UserLanguageApp
import com.example.abc_jobs_alpaca.model.models.UserTimeFormat
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import kotlinx.coroutines.launch

class PreferencesViewModel (private val application: Application, private val abcJobsRepository: ABCJobsRepository) : ViewModel() {

    var selectedLanguagePosition = MutableLiveData<Int>()
    val selectedDateFormatPosition = MutableLiveData<Int>()
    val selectedTimeFormatPosition = MutableLiveData<Int>()
    val preferencesUpdatedLiveData = MutableLiveData<Unit>()
    private val tokenLiveData = MutableLiveData<String?>()

    val languageSpinnerItems = listOf(UserLanguageApp.ES.formatString, UserLanguageApp.EN.formatString)
    val dateFormatSpinnerItems = listOf(UserDateFormat.AAAAMMDDSLASH.formatString,
        UserDateFormat.AAAAMMDDHYPHEN.formatString, UserDateFormat.DDMMAAAASLASH.formatString,
        UserDateFormat.DDMMAAAAHYPHEN.formatString)
    val timeFormatSpinnerItems = listOf(UserTimeFormat.FORMATOHORAS12.formatString, UserTimeFormat.FORMATOHORAS24.formatString)

    fun onTokenUpdated(token: String?) {
        tokenLiveData.value = token
    }

    suspend fun save(){
        val language = UserLanguageApp.values()[selectedLanguagePosition.value ?: 0]
        val dateFormat = UserDateFormat.values()[selectedDateFormatPosition.value ?: 0]
        val timeFormat = UserTimeFormat.values()[selectedTimeFormatPosition.value ?: 0]

        val token = tokenLiveData.value ?: return

        val newConfigRequest = ConfigRequest(language, dateFormat, timeFormat)
        abcJobsRepository.postConfig(token,newConfigRequest)
            .onSuccess {
                preferencesUpdatedLiveData.value = Unit
            }
            .onFailure { Log.d("PreferencesViewModel", "Error al guardar la configuración") }

    }
}

