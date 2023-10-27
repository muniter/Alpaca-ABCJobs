package com.example.abc_jobs_alpaca.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abc_jobs_alpaca.R


class HomeViewModel(application: Application) : ViewModel() {

    // Accede al valor desde strings.xml
    private val _text = MutableLiveData<String>().apply {
        value = application.getString(R.string.home_fragment_text)
    }

    val text: LiveData<String> = _text
}
