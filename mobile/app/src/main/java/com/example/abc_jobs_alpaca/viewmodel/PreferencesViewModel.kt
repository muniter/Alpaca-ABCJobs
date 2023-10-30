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


class PreferencesViewModel (private val application: Application) : ViewModel() {

    private val abcJobsRepository = ABCJobsRepository(application)

    // Selecciones del usuario en los Spinners
    var selectedLanguagePosition = MutableLiveData<Int>()
    val selectedDateFormatPosition = MutableLiveData<Int>()
    val selectedTimeFormatPosition = MutableLiveData<Int>()
    val preferencesUpdatedLiveData = MutableLiveData<Unit>()
    private val tokenLiveData = MutableLiveData<String?>()

    // Listas de opciones para los Spinners
    val languageSpinnerItems = listOf(UserLanguageApp.ES.formatString, UserLanguageApp.EN.formatString)
    val dateFormatSpinnerItems = listOf(UserDateFormat.AAAAMMDDSLASH.formatString,
        UserDateFormat.AAAAMMDDHYPHEN.formatString, UserDateFormat.DDMMAAAASLASH.formatString,
        UserDateFormat.DDMMAAAAHYPHEN.formatString)
    val timeFormatSpinnerItems = listOf(UserTimeFormat.FORMATOHORAS12.formatString, UserTimeFormat.FORMATOHORAS24.formatString)

    fun onTokenUpdated(token: String?) {
        tokenLiveData.value = token
    }

    fun observeToken(owner: LifecycleOwner, observer: Observer<String?>) {
        tokenLiveData.observe(owner, observer)
    }

    fun observePreferencesUpdated(owner: LifecycleOwner, observer: Observer<Unit>) {
        preferencesUpdatedLiveData.observe(owner, observer)
    }

    fun save(){
        val language = UserLanguageApp.values()[selectedLanguagePosition.value ?: 0]
        val dateFormat = UserDateFormat.values()[selectedDateFormatPosition.value ?: 0]
        val timeFormat = UserTimeFormat.values()[selectedTimeFormatPosition.value ?: 0]

        // get token
        val token = tokenLiveData.value
        if (token == null) {
            return
        }

        val newConfigRequest = ConfigRequest(language, dateFormat, timeFormat)
        viewModelScope.launch {
            abcJobsRepository.postConfig(token,newConfigRequest)
                .onSuccess {
                    preferencesUpdatedLiveData.value = Unit
                }
                .onFailure { Log.d("PreferencesViewModel", "Error al guardar la configuración") }

        }

    }


}

