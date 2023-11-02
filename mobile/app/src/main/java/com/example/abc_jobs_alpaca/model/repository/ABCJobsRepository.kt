package com.example.abc_jobs_alpaca.model.repository


import android.app.Application
import android.util.Log
import com.example.abc_jobs_alpaca.model.api.ABCJobsService
import com.example.abc_jobs_alpaca.model.models.*

class ABCJobsRepository(private val applicationContext: Application) {

    suspend fun postCandidate(newCandidate: UserRegisterRequest): Result<UserRegisterResponse> {
        val candidateJson = serializeCandidate(newCandidate)
        return ABCJobsService.getInstance(applicationContext).postCandidate(candidateJson)
    }

    suspend fun postLoginUser(loginCandidate: UserLoginRequest): Result<UserLoginResponse>{
        val loginUserJson = serializeLoginUser(loginCandidate)
        return ABCJobsService.getInstance(applicationContext).postLoginUser(loginUserJson)
    }

    suspend fun getConfig(token: String): Result<ConfigData> {
        return ABCJobsService.getInstance(applicationContext).getConfig(token)
    }

    suspend fun postConfig(token: String, configRequest: ConfigRequest): Result<ConfigData> {
        val configJson = serializeConfig(configRequest)
        return ABCJobsService.getInstance(applicationContext).postConfig(token, configJson)
    }

    suspend fun getTypeTitles(token: String): Result<AcademicInfoTypeResponse>{
        return ABCJobsService.getInstance(applicationContext).getTypesTitle(token)
    }

    suspend fun postAcademicInfo(token: String, newAcademicInfo: AcademicInfoRequest): Result<AcademicInfoResponse>{
        val academicInfoJson = serializeAcademicInfo(newAcademicInfo)
        return ABCJobsService.getInstance(applicationContext).postAcademicInfo(token, academicInfoJson)
    }

}