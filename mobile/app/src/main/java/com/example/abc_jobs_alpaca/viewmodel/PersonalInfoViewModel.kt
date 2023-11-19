package com.example.abc_jobs_alpaca.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abc_jobs_alpaca.model.models.Country
import com.example.abc_jobs_alpaca.model.models.PersonalInfo
import com.example.abc_jobs_alpaca.model.models.PersonalInfoRequest
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.utils.MessageEvent
import com.example.abc_jobs_alpaca.utils.MessageType
import java.time.format.DateTimeFormatter

class PersonalInfoViewModel(
    private val token: String,
    private val dateFormat: String,
    private val abcJobsRepository: ABCJobsRepository
) : ViewModel() {
    val parsedDate: MutableLiveData<String?> = MutableLiveData(null)
    var countrys: MutableLiveData<ArrayList<Country?>> = MutableLiveData(ArrayList())
    val personalInfo: MutableLiveData<PersonalInfo?> = MutableLiveData(null)

    suspend fun loadData() {

        var countries = ArrayList<Country?>()
        countries.add(Country(null, "", "", "-Seleccionar-", ""))

        abcJobsRepository.getCountries()
            .onSuccess {
                if (it != null) {
                    countries.addAll(it.data!!)
                }
                countrys.value = countries
            }
            .onFailure {
                countrys.value = countries
            }

        showForm.value = false
        enableForm.value = false

        abcJobsRepository.getPersonalInfo(token)
            .onSuccess {
                personalInfo.value = (it?.data)

                if (it?.data?.birth_date != null ||
                    it?.data?.country_code != null ||
                    it?.data?.city != null ||
                    it?.data?.address != null ||
                    it?.data?.phone != null ||
                    it?.data?.biography != null ||
                    it?.data?.languages != null
                ) {
                    showForm.value =true
                }

                if (it?.data?.birth_date != null) {
                    updateDateString()
                }

                if (it?.data?.country_code != null) {

                }
            }
            .onFailure {
                personalInfo.value = null
            }
    }

    fun updateDateString() {
        val dateFormatter = DateTimeFormatter.ofPattern(dateFormat)
        // Format the selected date into a string

        if (personalInfo.value?.birth_date != null) {
            val formattedDate = dateFormatter.format(personalInfo.value?.birth_date)

            parsedDate.value = formattedDate
        }
    }

    val showForm = MutableLiveData<Boolean>()
    val enableForm = MutableLiveData<Boolean>()

    private val messageLiveData = MutableLiveData<MessageEvent>()

    private val enabledElementsLiveData = MutableLiveData<Boolean>()

    fun getEnabledElementsLiveData(): LiveData<Boolean> {
        return enabledElementsLiveData
    }

    suspend fun savePersonalInfo(personalInfoRequest: PersonalInfoRequest) {
        val response = abcJobsRepository.postPersonalInfo(token, personalInfoRequest)

        response.onSuccess { it ->
            messageLiveData.postValue(MessageEvent(MessageType.SUCCESS, "Guardado"))
        }
    }

}