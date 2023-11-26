package com.example.abc_jobs_alpaca.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abc_jobs_alpaca.model.models.EmployeePerformance
import com.example.abc_jobs_alpaca.model.models.EvaluationEmployeeRequest
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.utils.MessageEvent
import com.example.abc_jobs_alpaca.utils.MessageType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PerformanceEmployeeViewModel (
    private val abcJobsRepository: ABCJobsRepository,
    private val employeeId: Int,
    private val fullName: String,
    private val result: Int
): ViewModel() {
    var employeeItem: MutableLiveData<EmployeePerformance?> = MutableLiveData(null)
    val messageLiveData = MutableLiveData<MessageEvent>()
    val isSaved = MutableLiveData(false)

    fun loadEmployeeData() {
        employeeItem.value = EmployeePerformance(
            employeeId, fullName, result
        )
    }

    private fun formatDate(date: Date): String {
        val pattern = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(date)
    }

    suspend fun savePerformanceEmployee(token: String, employeeId:Int, result: Int) {
        val date = formatDate(Date())
        var request = EvaluationEmployeeRequest(date, result)
        val response = abcJobsRepository.postEvaluateEmployee(
            token, employeeId, request)
        response.onSuccess {
            messageLiveData.postValue(MessageEvent(MessageType.SUCCESS, "Ok"))
            isSaved.postValue(true)
        }
    }
}