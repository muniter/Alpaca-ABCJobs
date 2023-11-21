package com.example.abc_jobs_alpaca.model.repository


import android.app.Application
import com.example.abc_jobs_alpaca.model.api.ABCJobsService
import com.example.abc_jobs_alpaca.model.api.ABCJobsServiceUtils
import com.example.abc_jobs_alpaca.model.models.AcademicInfoItemDeleteResponse
import com.example.abc_jobs_alpaca.model.models.AcademicInfoItemResponse
import com.example.abc_jobs_alpaca.model.models.AcademicInfoRequest
import com.example.abc_jobs_alpaca.model.models.AcademicInfoResponse
import com.example.abc_jobs_alpaca.model.models.AcademicInfoTypeResponse
import com.example.abc_jobs_alpaca.model.models.Answer
import com.example.abc_jobs_alpaca.model.models.AnswerQuestionResponse
import com.example.abc_jobs_alpaca.model.models.ConfigData
import com.example.abc_jobs_alpaca.model.models.ConfigRequest
import com.example.abc_jobs_alpaca.model.models.CountriesResponse
import com.example.abc_jobs_alpaca.model.models.ExamStartResponse
import com.example.abc_jobs_alpaca.model.models.ExamsExtendResponse
import com.example.abc_jobs_alpaca.model.models.ExamsResponse
import com.example.abc_jobs_alpaca.model.models.InterviewsResponse
import com.example.abc_jobs_alpaca.model.models.PersonalInfoRequest
import com.example.abc_jobs_alpaca.model.models.PersonalInfoResponse
import com.example.abc_jobs_alpaca.model.models.SkillInfoTypeResponse
import com.example.abc_jobs_alpaca.model.models.TeamsResponse
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoItemDeleteResponse
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoItemResponse
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoRequest
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoResponse
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoTypeResponse
import com.example.abc_jobs_alpaca.model.models.TechnicalProofRequest
import com.example.abc_jobs_alpaca.model.models.UserLoginRequest
import com.example.abc_jobs_alpaca.model.models.UserLoginResponse
import com.example.abc_jobs_alpaca.model.models.UserRegisterRequest
import com.example.abc_jobs_alpaca.model.models.UserRegisterResponse
import com.example.abc_jobs_alpaca.model.models.VacanciesResponse
import com.example.abc_jobs_alpaca.model.models.VacancyResponse
import com.example.abc_jobs_alpaca.model.models.WorkInfoItemDeleteResponse
import com.example.abc_jobs_alpaca.model.models.WorkInfoItemResponse
import com.example.abc_jobs_alpaca.model.models.WorkInfoRequest
import com.example.abc_jobs_alpaca.model.models.WorkInfoResponse
import com.example.abc_jobs_alpaca.model.models.serializeAcademicInfo
import com.example.abc_jobs_alpaca.model.models.serializeAnswer
import com.example.abc_jobs_alpaca.model.models.serializeCandidate
import com.example.abc_jobs_alpaca.model.models.serializeConfig
import com.example.abc_jobs_alpaca.model.models.serializeLoginUser
import com.example.abc_jobs_alpaca.model.models.serializePersonalInfo
import com.example.abc_jobs_alpaca.model.models.serializeTechnicalInfo
import com.example.abc_jobs_alpaca.model.models.serializeTechnicalProofRequest
import com.example.abc_jobs_alpaca.model.models.serializeWorkInfo

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

    suspend fun getAllExams(token: String): Result<ExamsResponse>{
        return ABCJobsService.getInstance(applicationContext).getAllExams(token)
    }

    suspend fun getAllExamsResult(token: String): Result<ExamsExtendResponse>{
        return ABCJobsService.getInstance(applicationContext).getAllExamsResults(token)
    }

    suspend fun postExamStart(token: String, examId: Int): Result<ExamStartResponse>{
        return ABCJobsService.getInstance(applicationContext).postStartExam(token, examId)
    }

    suspend fun postAnswerQuestion(token: String, idResult: Int, answerQuestionRequest: Answer): Result<AnswerQuestionResponse>{
        val answerQuestionJson = serializeAnswer(answerQuestionRequest)
        return ABCJobsService.getInstance(applicationContext).postAnswerQuestion(token, idResult ,answerQuestionJson)
    }

    suspend fun getAllInterviews(token: String): Result<InterviewsResponse>{
        return ABCJobsService.getInstance(applicationContext).getAllInterviews(token)
    }

    suspend fun getAllTeams(token: String): Result<TeamsResponse> {
        return ABCJobsService.getInstance(applicationContext).getAllTeams(token)
    }
    suspend fun getVacancies(token: String): Result<VacanciesResponse>{
        return ABCJobsService.getInstance(applicationContext).getAllVacancies(token)
    }

    suspend fun getVacancy(token: String, vacancyId: Int): Result<VacancyResponse>{
        return ABCJobsService.getInstance(applicationContext).getVacancy(token,vacancyId)
    }

    suspend fun postTestResult(token: String, vacancyId: Int, request: ArrayList<TechnicalProofRequest>): Result<VacancyResponse>{
        val requestJson = serializeTechnicalProofRequest(request)
        return ABCJobsService.getInstance(applicationContext).postTestResult(token, vacancyId, requestJson)
    }

}