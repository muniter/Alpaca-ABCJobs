package com.example.abc_jobs_alpaca.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abc_jobs_alpaca.model.models.ShortlistedCandidateItem
import com.example.abc_jobs_alpaca.model.models.TechnicalProofRequest
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.utils.MessageEvent
import com.example.abc_jobs_alpaca.utils.MessageType

class TechnicalProofViewModel(
    private val abcJobsRepository: ABCJobsRepository,
    private val fullName: String,
    private val country: String,
    private val city: String,
    private val result: Int
) : ViewModel() {

    val shortlistedCandidateItem: MutableLiveData<ShortlistedCandidateItem?> = MutableLiveData(null)
    private val messageLiveData = MutableLiveData<MessageEvent>()

    fun loadTechnicalProofData() {
        shortlistedCandidateItem.value = ShortlistedCandidateItem(
            0, 0, "", "", fullName,
            "", "", 0, country, city, "",
            "", "", listOf(), result
        )
    }

    suspend fun saveTechnicalProofResult(token: String, vacancyId:Int, request: ArrayList<TechnicalProofRequest>) {
        val response = abcJobsRepository.postTestResult(token, vacancyId, request)
        response.onSuccess { it ->
            messageLiveData.postValue(MessageEvent(MessageType.SUCCESS, "Guardado"))
        }
    }

    suspend fun selectCandidate(token: String, vacancyId: Int, candidateId: Int) {
        val response = abcJobsRepository.postSelectCandidate(token, vacancyId, candidateId)
        response.onSuccess { _ ->
            messageLiveData.postValue(MessageEvent(MessageType.SUCCESS, "Guardado"))
        }
        response.onFailure { _ ->
            messageLiveData.postValue(MessageEvent(MessageType.ERROR, "Error"))
        }
    }

}