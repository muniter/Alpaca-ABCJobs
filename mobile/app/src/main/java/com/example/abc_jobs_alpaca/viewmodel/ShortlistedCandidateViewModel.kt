package com.example.abc_jobs_alpaca.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abc_jobs_alpaca.model.models.ShortlistedCandidateItem
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository

class ShortlistedCandidateViewModel(private val abcJobsRepository: ABCJobsRepository, private val vacancyId: Int) : ViewModel() {

    private val tokenLiveData = MutableLiveData<String?>()
    val token = tokenLiveData
    private val _shortlistedCandidateList = MutableLiveData<List<ShortlistedCandidateItem>?>()
    val shortlistedCandidateList: MutableLiveData<List<ShortlistedCandidateItem>?> get() = _shortlistedCandidateList

    private val _text = MutableLiveData<String>().apply {
        value = "This is shortlistedCandidate Fragment"
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

    suspend fun loadShortlistedCandidateItems() {
        try {
            if (token.value != null) {
                abcJobsRepository.getVacancy(token.value!!,vacancyId)
                    .onSuccess { response ->
                        if (response.success) {
                            _shortlistedCandidateList.value = response.data?.preselection
                            navigationListener?.navigateToNextScreen()
                        }
                    }
                    .onFailure {
                        //TODO: something
                    }
            } else {
                //TODO: message
            }
        } catch (e: Exception) {
            //TODO: exception
        }

    }

}