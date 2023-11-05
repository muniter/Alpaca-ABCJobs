package com.example.abc_jobs_alpaca.model.repository


import android.app.Application
import android.util.Log
import com.example.abc_jobs_alpaca.model.api.ABCJobsService
import com.example.abc_jobs_alpaca.model.api.ABCJobsServiceUtils
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
        return ABCJobsServiceUtils.getInstance(applicationContext).getTypesTitle(token)
    }
    suspend fun getTechnicalInfoTypes(token: String): Result<TechnicalInfoTypeResponse>{
        return ABCJobsServiceUtils.getInstance(applicationContext).getTechnicalInfoTypes(token)
    }

    suspend fun postAcademicInfo(token: String, newAcademicInfo: AcademicInfoRequest): Result<AcademicInfoItemResponse>{
        val academicInfoJson = serializeAcademicInfo(newAcademicInfo)
        return ABCJobsService.getInstance(applicationContext).postAcademicInfo(token, academicInfoJson)
    }

    suspend fun getAcademicInfo(token: String): Result<AcademicInfoResponse>{
        return ABCJobsService.getInstance(applicationContext).getAcademicInfo(token)
    }

    suspend fun deleteAcademicInfo(token: String, idAcademicInfo: Int): Result<AcademicInfoItemDeleteResponse>{
        return ABCJobsService.getInstance(applicationContext).deleteAcademicInfoItem(token, idAcademicInfo)
    }
    suspend fun postTechnicalInfo(token: String, newTechnicalInfo: TechnicalInfoRequest): Result<TechnicalInfoItemResponse>{
        val technicalInfoJson = serializeTechnicalInfo(newTechnicalInfo)
        return ABCJobsService.getInstance(applicationContext).postTechnicalInfo(token, technicalInfoJson)
    }
    suspend fun getTechnicalInfo(token: String): Result<TechnicalInfoResponse>{
        return ABCJobsService.getInstance(applicationContext).getTechnicalInfo(token)
    }

    suspend fun deleteTechnicalInfo(token: String, idTechnicalInfo: Int): Result<TechnicalInfoItemDeleteResponse>{
        return ABCJobsService.getInstance(applicationContext).deleteTechnicalInfo(token, idTechnicalInfo)
    }

}