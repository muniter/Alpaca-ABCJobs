package com.example.abc_jobs_alpaca.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abc_jobs_alpaca.model.models.Employee
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.view.EmployeeListFragment

class EmployeeListViewModel(private val abcJobsRepository: ABCJobsRepository) : ViewModel() {

    private val tokenLiveData = MutableLiveData<String?>()
    val token = tokenLiveData
    private val _employeeList = MutableLiveData<List<Employee>?>()
    val employeeList: MutableLiveData<List<Employee>?> get() = _employeeList

    fun onTokenUpdated(token: String?) {
        tokenLiveData.value = token
    }
    interface NavigationListener {
        fun navigateToNextScreen()
    }

    private var navigationListener: EmployeeListViewModel.NavigationListener? = null

    fun setNavigationListener(listener: EmployeeListViewModel.NavigationListener) {
        navigationListener = listener
    }

    suspend fun loadEmployeeItems() {
        try {
            if (token?.value != null) {
                abcJobsRepository.getHiredEmployees(token.value!!)
                    .onSuccess { response ->
                        if (response.success) {
                            _employeeList.value = response.data
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