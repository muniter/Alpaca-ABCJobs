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

    suspend fun getPersonalInfo(token: String): Result<PersonalInfoResponse?> {
        return ABCJobsService.getInstance(applicationContext).getPersonalInfo(token)
    }

    suspend fun postPersonalInfo(token: String, personalInfoRequest: PersonalInfoRequest): Result<PersonalInfoResponse?> {
        val configJson = serializePersonalInfo(personalInfoRequest)

        return ABCJobsService.getInstance(applicationContext).postPersonalInfo(token, configJson)
    }

    suspend fun getTypeTitles(token: String): Result<AcademicInfoTypeResponse>{
        return ABCJobsServiceUtils.getInstance(applicationContext).getTypesTitle(token)
    }
    suspend fun getTechnicalInfoTypes(token: String): Result<TechnicalInfoTypeResponse>{
        return ABCJobsServiceUtils.getInstance(applicationContext).getTechnicalInfoTypes(token)
    }

    suspend fun getTypesSkill(token: String): Result<SkillInfoTypeResponse>{
        return ABCJobsServiceUtils.getInstance(applicationContext).getSkillsTypes(token)
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

    suspend fun postWorkInfo(token: String, newWorkInfo: WorkInfoRequest): Result<WorkInfoItemResponse>{
        val workInfoJson = serializeWorkInfo(newWorkInfo)
        return ABCJobsService.getInstance(applicationContext).postWorkInfo(token, workInfoJson)
    }

    suspend fun getWorkInfo(token: String): Result<WorkInfoResponse>{
        return ABCJobsService.getInstance(applicationContext).getWorkInfo(token)
    }

    suspend fun deleteWorkInfo(token: String, idWorkInfo: Int): Result<WorkInfoItemDeleteResponse>{
        return ABCJobsService.getInstance(applicationContext).deleteWorkInfo(token, idWorkInfo)
    }

    suspend fun getCountries(): Result<CountriesResponse> {
        return ABCJobsService.getInstance(applicationContext).getCountries()
    }

    suspend fun getAllExams(token: String): Result<ExamItemResponse>{
        return ABCJobsService.getInstance(applicationContext).getAllExams(token)
    }

}