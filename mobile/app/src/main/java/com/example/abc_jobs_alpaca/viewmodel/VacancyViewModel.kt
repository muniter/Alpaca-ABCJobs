package com.example.abc_jobs_alpaca.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abc_jobs_alpaca.model.models.VacancyItem
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository

class VacancyViewModel(private val abcJobsRepository: ABCJobsRepository) : ViewModel() {

    private val tokenLiveData = MutableLiveData<String?>()
    val token = tokenLiveData
    private val _vacancyList = MutableLiveData<List<VacancyItem>?>()
    val vacancyList: MutableLiveData<List<VacancyItem>?> get() = _vacancyList

    private val _text = MutableLiveData<String>().apply {
        value = "This is vacancy Fragment"
    }
    val text: LiveData<String> = _text
    fun onTokenUpdated(token: String?) {
        tokenLiveData.value = token
    }

    interface NavigationListener {
        fun navigateToNextScreen()
    }

    private var navigationListener: NavigationListener? = null

    fun setNavigationListener(listener: NavigationListener) {
        navigationListener = listener
    }

    suspend fun loadVacancyItems() {
        try {
            if (token.value != null) {
                abcJobsRepository.getVacancies(token.value!!)
                    .onSuccess { response ->
                        if (response.success) {
                            var list = mutableListOf<VacancyItem>()
                            response.data?.forEach {
                                if(it.open){
                                    list.add(it)
                                }
                            }
                            _vacancyList.value = list
                        }
                    }
                    .onFailure {
                        //TODO: something
                    }
            } else {
                //TODO: message
            }
        } catch (e: Exception) {

        }

    }

}