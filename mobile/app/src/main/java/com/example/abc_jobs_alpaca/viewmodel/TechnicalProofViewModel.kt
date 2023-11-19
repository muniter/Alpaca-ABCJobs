package com.example.abc_jobs_alpaca.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abc_jobs_alpaca.model.models.ShortlistedCandidateItem
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository

class TechnicalProofViewModel(
    private val abcJobsRepository: ABCJobsRepository,
    private val fullName: String,
    private val country: String,
    private val city: String,
    private val result: Int
) : ViewModel() {

    val shortlistedCandidateItem: MutableLiveData<ShortlistedCandidateItem?> = MutableLiveData(null)

    fun loadTechnicalProofData() {
        shortlistedCandidateItem.value = ShortlistedCandidateItem(
            0, 0, "", "", fullName,
            "", "", 0, country, city, "",
            "", "", listOf(), result
        )
    }
}