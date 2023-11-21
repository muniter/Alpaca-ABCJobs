package com.example.abc_jobs_alpaca.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abc_jobs_alpaca.model.models.Team
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository

class TeamListViewModel(private val abcJobsRepository: ABCJobsRepository): ViewModel() {
    private val tokenLiveData = MutableLiveData<String?>()
    val token = tokenLiveData
    private val _teamList = MutableLiveData<List<Team>?>()
    val teamList: MutableLiveData<List<Team>?> get() = _teamList

    fun onTokenUpdated(token: String?) {
        tokenLiveData.value = token
    }

    suspend fun loadTeams(){
        try{
            if(token?.value != null){
                abcJobsRepository.getAllTeams(token.value!!)
                    .onSuccess { response ->
                        if(response.success){
                            _teamList.value = response.data
                        }
                    }
                    .onFailure {
                        _teamList.value = null
                    }
            }
        }
        catch (e: Exception){
            _teamList.value = null
        }
    }
}