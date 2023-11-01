package com.example.abc_jobs_alpaca.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository

class AcademicInfoViewModel (private val abcJobsRepository: ABCJobsRepository) : ViewModel(){
    private val _text = MutableLiveData<String>().apply {
        value = "This is academic info Fragment"
    }
    val text: LiveData<String> = _text
}