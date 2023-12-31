package com.example.abc_jobs_alpaca.model.api

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.abc_jobs_alpaca.model.models.AcademicInfoItemDeleteResponse
import com.example.abc_jobs_alpaca.model.models.AcademicInfoItemResponse
import com.example.abc_jobs_alpaca.model.models.AcademicInfoResponse
import com.example.abc_jobs_alpaca.model.models.AcademicInfoTypeResponse
import com.example.abc_jobs_alpaca.model.models.AnswerQuestionResponse
import com.example.abc_jobs_alpaca.model.models.ConfigData
import com.example.abc_jobs_alpaca.model.models.CountriesResponse
import com.example.abc_jobs_alpaca.model.models.Employee
import com.example.abc_jobs_alpaca.model.models.EmployeeResponse
import com.example.abc_jobs_alpaca.model.models.EmployeesResponse
import com.example.abc_jobs_alpaca.model.models.EvaluationEmployeeRequest
import com.example.abc_jobs_alpaca.model.models.ExamStartResponse
import com.example.abc_jobs_alpaca.model.models.ExamsExtendResponse
import com.example.abc_jobs_alpaca.model.models.ExamsResponse
import com.example.abc_jobs_alpaca.model.models.InterviewsResponse
import com.example.abc_jobs_alpaca.model.models.PersonalInfoResponse
import com.example.abc_jobs_alpaca.model.models.TeamsResponse
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoItemDeleteResponse
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoItemResponse
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoResponse
import com.example.abc_jobs_alpaca.model.models.UserDateFormat
import com.example.abc_jobs_alpaca.model.models.UserLanguageApp
import com.example.abc_jobs_alpaca.model.models.UserLoginResponse
import com.example.abc_jobs_alpaca.model.models.UserRegisterResponse
import com.example.abc_jobs_alpaca.model.models.UserTimeFormat
import com.example.abc_jobs_alpaca.model.models.VacanciesResponse
import com.example.abc_jobs_alpaca.model.models.VacancyResponse
import com.example.abc_jobs_alpaca.model.models.VacancySelectCandidateResponse
import com.example.abc_jobs_alpaca.model.models.WorkInfoItemDeleteResponse
import com.example.abc_jobs_alpaca.model.models.WorkInfoItemResponse
import com.example.abc_jobs_alpaca.model.models.WorkInfoResponse
import com.example.abc_jobs_alpaca.model.models.deserializeAcademicInfo
import com.example.abc_jobs_alpaca.model.models.deserializeAcademicInfoError
import com.example.abc_jobs_alpaca.model.models.deserializeAcademicInfoItem
import com.example.abc_jobs_alpaca.model.models.deserializeAcademicInfoItemDelete
import com.example.abc_jobs_alpaca.model.models.deserializeAcademicInfoItemDeleteError
import com.example.abc_jobs_alpaca.model.models.deserializeAnswerQuestion
import com.example.abc_jobs_alpaca.model.models.deserializeAnswerQuestionError
import com.example.abc_jobs_alpaca.model.models.deserializeCandidate
import com.example.abc_jobs_alpaca.model.models.deserializeCandidateError
import com.example.abc_jobs_alpaca.model.models.deserializeCountries
import com.example.abc_jobs_alpaca.model.models.deserializeCountriesError
import com.example.abc_jobs_alpaca.model.models.deserializeEmployeeError
import com.example.abc_jobs_alpaca.model.models.deserializeEmployeeResponse
import com.example.abc_jobs_alpaca.model.models.deserializeEmployeesError
import com.example.abc_jobs_alpaca.model.models.deserializeEmployeesResponse
import com.example.abc_jobs_alpaca.model.models.deserializeExamStart
import com.example.abc_jobs_alpaca.model.models.deserializeExamStartError
import com.example.abc_jobs_alpaca.model.models.deserializeExams
import com.example.abc_jobs_alpaca.model.models.deserializeExamsResult
import com.example.abc_jobs_alpaca.model.models.deserializeInterviews
import com.example.abc_jobs_alpaca.model.models.deserializeLoginCandidate
import com.example.abc_jobs_alpaca.model.models.deserializeLoginCandidateError
import com.example.abc_jobs_alpaca.model.models.deserializeLoginCompany
import com.example.abc_jobs_alpaca.model.models.deserializePersonalInfo
import com.example.abc_jobs_alpaca.model.models.deserializePersonalInfoError
import com.example.abc_jobs_alpaca.model.models.deserializeTeams
import com.example.abc_jobs_alpaca.model.models.deserializeTechnicalInfo
import com.example.abc_jobs_alpaca.model.models.deserializeTechnicalInfoItem
import com.example.abc_jobs_alpaca.model.models.deserializeTechnicalInfoItemDelete
import com.example.abc_jobs_alpaca.model.models.deserializeTechnicalInfoItemDeleteError
import com.example.abc_jobs_alpaca.model.models.deserializeTechnicalInfoItemError
import com.example.abc_jobs_alpaca.model.models.deserializeTechnicalProofError
import com.example.abc_jobs_alpaca.model.models.deserializeTypesTitles
import com.example.abc_jobs_alpaca.model.models.deserializeTypesTitlesError
import com.example.abc_jobs_alpaca.model.models.deserializeVacancies
import com.example.abc_jobs_alpaca.model.models.deserializeVacancy
import com.example.abc_jobs_alpaca.model.models.deserializeVacancyError
import com.example.abc_jobs_alpaca.model.models.deserializeVacancySelectCandidateError
import com.example.abc_jobs_alpaca.model.models.deserializeVacancySelectCandidateResponse
import com.example.abc_jobs_alpaca.model.models.deserializeWorkInfo
import com.example.abc_jobs_alpaca.model.models.deserializeWorkInfoItem
import com.example.abc_jobs_alpaca.model.models.deserializeWorkInfoItemDelete
import com.example.abc_jobs_alpaca.model.models.deserializeWorkInfoItemDeleteError
import com.example.abc_jobs_alpaca.model.models.deserializeWorkInfoItemError
import com.example.abc_jobs_alpaca.model.models.serializeEvaluationEmployeeRequest
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import org.json.JSONArray
import org.json.JSONObject

class ABCJobsService constructor(context: Context) {

    companion object {
        private var BASEURL = "https://api.abc.muniter.link"
        private var CANDIDATES_PATH = "/candidatos"
        private var COMPANIES_PATH = "/empresas"
        private var USERS_PATH = "/usuarios"
        private var CREATE_PATH = "/crear"
        private var LOGIN_PATH = "/login"
        private var CONFIG_PATH = "/config"
        private var ACADEMIC_INFO_PATH = "/academic-info"
        private var TECHNICAL_INFO_PATH = "/technical-info"
        private var WORK_INFO_PATH = "/work-info"
        private var PERSONAL_INFO_PATH = "/personal-info"
        private var UTILS_PATH = "/utils"
        private var COUNTRIES_PATH = "/countries"
        private var TITLE_TYPES_PATH = "/title-types"
        private var EVALUATIONS_PATH = "/evaluaciones"
        private var EXAMS_PATH = "/exam"
        private var EXAM_RESULTS_PATH = "/exam-result"
        private var EXAM_ACTION_START = "/start"
        private var EXAM_ACTION_ANSWER = "/answer"
        private var INTERVIEWS_PATH = "/interviews"
        private var TEAM_PATH = "/team"
        private var VACANCY_PATH = "/vacancy"
        private var TEST_RESULT_PATH = "/test-result"
        private var SELECT_CANDIDATE_PATH = "/select"
        private var EMPLOYEE_PATH = "/employee"
        private var HIRED_EMPLOYEES_PATH = "$EMPLOYEE_PATH?hired_abc=true"

        private var EVALUATION_PATH = "/evaluation"
        private var instance: ABCJobsService? = null

        fun getInstance(context: Context) =
                instance
                        ?: synchronized(this) {
                            instance ?: ABCJobsService(context).also { instance = it }
                        }
    }
    val requestQueue: RequestQueue by lazy { Volley.newRequestQueue(context.applicationContext) }

    private fun postRequest(
            path: String,
            action: String,
            body: JSONObject,
            responseListener: Response.Listener<String>,
            errorListener: Response.ErrorListener,
    ): StringRequest {
        return object :
                StringRequest(
                        Method.POST,
                        BASEURL + path + action,
                        responseListener,
                        errorListener
                ) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
            override fun getBody(): ByteArray {
                Log.d("Sending body", body.toString())
                return body.toString().toByteArray()
            }
        }
    }

    private suspend fun fetchInfo(token: String, path: String, action: String): JSONObject {
        return suspendCoroutine { cont ->
            requestQueue.add(
                    object :
                            StringRequest(
                                    Method.GET,
                                    BASEURL + path + action,
                                    { response -> cont.resume(JSONObject(response)) },
                                    { volleyError -> cont.resumeWithException(volleyError) }
                            ) {
                        override fun getBodyContentType(): String {
                            return "application/json; charset=utf-8"
                        }

                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Authorization"] = "Bearer $token"
                            return headers
                        }
                    }
            )
        }
    }

    private fun requestWithToken(
            token: String,
            method: Int,
            path: String,
            action: String,
            body: JSONObject,
            responseListener: Response.Listener<String>,
            errorListener: Response.ErrorListener,
    ): StringRequest {
        return object :
                StringRequest(method, BASEURL + path + action, responseListener, errorListener) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                Log.d("Sending body", body.toString())
                return body.toString().toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
    }

    private fun requestArrayWithToken(
            token: String,
            method: Int,
            path: String,
            action: String,
            body: JSONArray,
            responseListener: Response.Listener<String>,
            errorListener: Response.ErrorListener,
    ): StringRequest {
        return object :
                StringRequest(method, BASEURL + path + action, responseListener, errorListener) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                Log.d("Sending body", body.toString())
                return body.toString().toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
    }

    suspend fun postCandidate(newCandidate: JSONObject): Result<UserRegisterResponse> {
        return try {
            val response =
                    suspendCoroutine<JSONObject> { cont ->
                        requestQueue.add(
                                postRequest(
                                        CANDIDATES_PATH,
                                        CREATE_PATH,
                                        newCandidate,
                                        { response -> cont.resume(JSONObject(response)) },
                                        { volleyError ->
                                            if (volleyError.networkResponse != null) {
                                                val errorData =
                                                        String(
                                                                volleyError.networkResponse.data,
                                                                Charsets.UTF_8
                                                        )
                                                val jsonError = JSONObject(errorData)

                                                if (!jsonError.optBoolean("success")) {
                                                    val candidateError =
                                                            deserializeCandidateError(jsonError)
                                                    cont.resumeWithException(candidateError)
                                                }
                                            } else {
                                                cont.resumeWithException(volleyError)
                                            }
                                        }
                                )
                        )
                    }
            if (response.optBoolean("success")) {
                val candidate = deserializeCandidate(response)
                Result.success(candidate)
            } else {
                val candidateError = deserializeCandidateError(response)
                Result.failure(candidateError)
            }
        } catch (e: Exception) {
            Log.d("NETWORK_ERROR", e.toString())
            Result.failure(e)
        }
    }

    suspend fun postLoginUser(loginUserJson: JSONObject): Result<UserLoginResponse> {
        return try {
            val response =
                    suspendCoroutine<JSONObject> { cont ->
                        requestQueue.add(
                                postRequest(
                                        USERS_PATH,
                                        LOGIN_PATH,
                                        loginUserJson,
                                        { response -> cont.resume(JSONObject(response)) },
                                        { volleyError ->
                                            if (volleyError.networkResponse != null) {
                                                val errorData =
                                                        String(
                                                                volleyError.networkResponse.data,
                                                                Charsets.UTF_8
                                                        )
                                                val jsonError = JSONObject(errorData)

                                                if (!jsonError.optBoolean("success")) {
                                                    val userLoginResponseError =
                                                            deserializeLoginCandidateError(
                                                                    jsonError
                                                            )
                                                    cont.resumeWithException(userLoginResponseError)
                                                }
                                            } else {
                                                cont.resumeWithException(volleyError)
                                            }
                                        }
                                )
                        )
                    }
            if (response.getBoolean("success")) {
                val data = response.optJSONObject("data")
                val usuario = data?.optJSONObject("usuario")
                val idEmpresa = usuario?.optInt("id_empresa")
                val idCandidato = usuario?.optInt("id_candidato")
                if (idCandidato != 0) {
                    val userLoginResponse = deserializeLoginCandidate(response)
                    Result.success(userLoginResponse)
                } else if (idEmpresa != 0) {
                    val userLoginResponse = deserializeLoginCompany(response)
                    Result.success(userLoginResponse)
                } else {
                    val userLoginResponseError = deserializeLoginCandidateError(response)
                    Result.failure(userLoginResponseError)
                }
            } else {
                val userLoginResponseError = deserializeLoginCandidateError(response)
                Result.failure(userLoginResponseError)
            }
        } catch (e: Exception) {
            Log.d("NETWORK_ERROR", e.toString())
            Result.failure(e)
        }
    }

    suspend fun getConfig(token: String): Result<ConfigData> {
        if (token.isEmpty()) {
            return Result.failure(Exception("El token está vacío"))
        }
        return try {
            val response = fetchInfo(token, USERS_PATH, CONFIG_PATH)
            Result.success(handleConfigResponse(response).getOrThrow())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun postConfig(token: String, configJson: JSONObject): Result<ConfigData> {
        return try {
            val response = suspendCoroutine { cont ->
                requestQueue.add(
                        requestWithToken(
                                token,
                                Request.Method.POST,
                                USERS_PATH,
                                CONFIG_PATH,
                                configJson,
                                { response -> cont.resume(JSONObject(response)) },
                                { volleyError -> cont.resumeWithException(volleyError) }
                        )
                )
            }
            Result.success(handleConfigResponse(response).getOrThrow())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun handleConfigResponse(response: JSONObject): Result<ConfigData> {
        try {
            val success = response.getBoolean("success")
            if (success) {
                val dataObject = response.optJSONObject("data")
                val configObject = dataObject?.optJSONObject("config")

                if (configObject != null) {
                    val languageAppString = configObject.optString("languageApp")
                    val timeFormatString = configObject.optString("timeFormat")
                    val dateFormatString = configObject.optString("dateFormat")

                    val languageAppStringMap =
                            UserLanguageApp.values().find { it.name == languageAppString }
                    val timeFormatStringMap =
                            UserTimeFormat.values().find { it.formatString == timeFormatString }
                    val dateFormatStringMap =
                            UserDateFormat.values().find { it.formatString == dateFormatString }

                    val configData =
                            ConfigData(
                                    languageAppStringMap!!,
                                    timeFormatStringMap!!,
                                    dateFormatStringMap!!
                            )

                    return Result.success(configData)
                }
            }
            return Result.failure(Exception("Error en la respuesta del servidor"))
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    suspend fun postAcademicInfo(
            token: String,
            academicInfoItem: JSONObject
    ): Result<AcademicInfoItemResponse> {
        return try {
            val response =
                    suspendCoroutine<JSONObject> { cont ->
                        requestQueue.add(
                                requestWithToken(
                                        token,
                                        Request.Method.POST,
                                        CANDIDATES_PATH,
                                        ACADEMIC_INFO_PATH,
                                        academicInfoItem,
                                        { response -> cont.resume(JSONObject(response)) },
                                        { volleyError ->
                                            if (volleyError.networkResponse != null) {
                                                val errorData =
                                                        String(
                                                                volleyError.networkResponse.data,
                                                                Charsets.UTF_8
                                                        )
                                                val jsonError = JSONObject(errorData)

                                                if (!jsonError.optBoolean("success")) {
                                                    val academicInfoError =
                                                            deserializeAcademicInfoError(jsonError)
                                                    cont.resumeWithException(academicInfoError)
                                                }
                                            } else {
                                                cont.resumeWithException(volleyError)
                                            }
                                        }
                                )
                        )
                    }
            if (response.getBoolean("success")) {
                val academicInfo = deserializeAcademicInfoItem(response)
                Result.success(academicInfo)
            } else {
                val academicInfoError = deserializeAcademicInfoError(response)
                Result.failure(academicInfoError)
            }
        } catch (e: Exception) {
            Log.d("NETWORK_ERROR", e.toString())
            Result.failure(e)
        }
    }

    suspend fun getAcademicInfo(token: String): Result<AcademicInfoResponse> {
        return try {
            val response = fetchInfo(token, CANDIDATES_PATH, ACADEMIC_INFO_PATH)
            Result.success(deserializeAcademicInfo(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAcademicInfoItem(
            token: String,
            id: Int
    ): Result<AcademicInfoItemDeleteResponse> {
        return try {
            val response =
                    suspendCoroutine<JSONObject> { cont ->
                        requestQueue.add(
                                requestWithToken(
                                        token,
                                        Request.Method.DELETE,
                                        CANDIDATES_PATH,
                                        "$ACADEMIC_INFO_PATH/$id",
                                        JSONObject(),
                                        { response -> cont.resume(JSONObject(response)) },
                                        { volleyError ->
                                            if (volleyError.networkResponse != null) {
                                                val errorData =
                                                        String(
                                                                volleyError.networkResponse.data,
                                                                Charsets.UTF_8
                                                        )
                                                val jsonError = JSONObject(errorData)

                                                if (!jsonError.optBoolean("success")) {
                                                    val academicInfoError =
                                                            deserializeAcademicInfoItemDeleteError(
                                                                    jsonError
                                                            )
                                                    cont.resumeWithException(academicInfoError)
                                                }
                                            } else {
                                                cont.resumeWithException(volleyError)
                                            }
                                        }
                                )
                        )
                    }
            if (response.getBoolean("success")) {
                val academicInfo = deserializeAcademicInfoItemDelete(response)
                Result.success(academicInfo)
            } else {
                val academicInfoError = deserializeAcademicInfoItemDeleteError(response)
                Result.failure(academicInfoError)
            }
        } catch (e: Exception) {
            Log.d("NETWORK_ERROR", e.toString())
            Result.failure(e)
        }
    }

    suspend fun postPersonalInfo(
            token: String,
            personalInfo: JSONObject
    ): Result<PersonalInfoResponse?> {
        return try {
            val response =
                    suspendCoroutine<JSONObject> { cont ->
                        requestQueue.add(
                                object :
                                        StringRequest(
                                                Method.POST,
                                                BASEURL + CANDIDATES_PATH + PERSONAL_INFO_PATH,
                                                { response -> cont.resume(JSONObject(response)) },
                                                { volleyError ->
                                                    cont.resumeWithException(volleyError)
                                                }
                                        ) {
                                    override fun getBodyContentType(): String {
                                        return "application/json; charset=utf-8"
                                    }

                                    override fun getBody(): ByteArray {
                                        Log.d("Sending body", personalInfo.toString())
                                        return personalInfo.toString().toByteArray()
                                    }

                                    override fun getHeaders(): MutableMap<String, String> {
                                        val headers = HashMap<String, String>()
                                        headers["Authorization"] = "Bearer $token"
                                        return headers
                                    }
                                }
                        )
                    }
            if (response.getBoolean("success")) {
                val personalInfo = deserializePersonalInfo(response)
                Result.success(personalInfo)
            } else {
                val personalInfoError = deserializeAcademicInfoError(response)
                Result.failure(personalInfoError)
            }
        } catch (e: Exception) {
            Log.d("NETWORK_ERROR", e.toString())
            Result.failure(e)
        }
    }

    suspend fun getPersonalInfo(token: String): Result<PersonalInfoResponse?> {
        return try {
            val response =
                    suspendCoroutine<JSONObject> { cont ->
                        requestQueue.add(
                                object :
                                        StringRequest(
                                                Method.GET,
                                                BASEURL + CANDIDATES_PATH + PERSONAL_INFO_PATH,
                                                { response -> cont.resume(JSONObject(response)) },
                                                { volleyError ->
                                                    cont.resumeWithException(volleyError)
                                                }
                                        ) {
                                    override fun getBodyContentType(): String {
                                        return "application/json; charset=utf-8"
                                    }

                                    override fun getHeaders(): MutableMap<String, String> {
                                        val headers = HashMap<String, String>()
                                        headers["Authorization"] = "Bearer $token"
                                        return headers
                                    }
                                }
                        )
                    }
            if (response.getBoolean("success")) {
                val personalInfo = deserializePersonalInfo(response)
                Result.success(personalInfo)
            } else {
                val personalInfoError = deserializePersonalInfoError(response)
                Result.failure(personalInfoError)
            }
        } catch (e: Exception) {
            Log.d("NETWORK_ERROR", e.toString())
            Result.failure(e)
        }
    }

    suspend fun getTypesTitle(token: String): Result<AcademicInfoTypeResponse> {
        return try {
            val response =
                    suspendCoroutine<JSONObject> { cont ->
                        requestQueue.add(
                                object :
                                        StringRequest(
                                                Method.GET,
                                                BASEURL +
                                                        CANDIDATES_PATH +
                                                        UTILS_PATH +
                                                        TITLE_TYPES_PATH,
                                                { response -> cont.resume(JSONObject(response)) },
                                                { volleyError ->
                                                    cont.resumeWithException(volleyError)
                                                }
                                        ) {
                                    override fun getBodyContentType(): String {
                                        return "application/json; charset=utf-8"
                                    }

                                    override fun getHeaders(): MutableMap<String, String> {
                                        val headers = HashMap<String, String>()
                                        headers["Authorization"] = "Bearer $token"
                                        return headers
                                    }
                                }
                        )
                    }
            if (response.getBoolean("success")) {
                val typesTitle = deserializeTypesTitles(response)
                Result.success(typesTitle)
            } else {
                val typesTitleError = deserializeTypesTitlesError(response)
                Result.failure(typesTitleError)
            }
        } catch (e: Exception) {
            Log.d("NETWORK_ERROR", e.toString())
            Result.failure(e)
        }
    }

    suspend fun postTechnicalInfo(
            token: String,
            technicalInfoItem: JSONObject
    ): Result<TechnicalInfoItemResponse> {
        return try {
            val response =
                    suspendCoroutine<JSONObject> { cont ->
                        requestQueue.add(
                                requestWithToken(
                                        token,
                                        Request.Method.POST,
                                        CANDIDATES_PATH,
                                        TECHNICAL_INFO_PATH,
                                        technicalInfoItem,
                                        { response -> cont.resume(JSONObject(response)) },
                                        { volleyError ->
                                            if (volleyError.networkResponse != null) {
                                                val errorData =
                                                        String(
                                                                volleyError.networkResponse.data,
                                                                Charsets.UTF_8
                                                        )
                                                val jsonError = JSONObject(errorData)

                                                if (!jsonError.optBoolean("success")) {
                                                    val technicalInfoError =
                                                            deserializeTechnicalInfoItemError(
                                                                    jsonError
                                                            )
                                                    cont.resumeWithException(technicalInfoError)
                                                }
                                            } else {
                                                cont.resumeWithException(volleyError)
                                            }
                                        }
                                )
                        )
                    }
            if (response.getBoolean("success")) {
                val technicalInfo = deserializeTechnicalInfoItem(response)
                Result.success(technicalInfo)
            } else {
                val technicalInfoError = deserializeTechnicalInfoItemError(response)
                Result.failure(technicalInfoError)
            }
        } catch (e: Exception) {
            Log.d("NETWORK_ERROR", e.toString())
            Result.failure(e)
        }
    }

    suspend fun getCountries(): Result<CountriesResponse> {
        return try {
            val response =
                    suspendCoroutine<JSONObject> { cont ->
                        requestQueue.add(
                                object :
                                        StringRequest(
                                                Method.GET,
                                                BASEURL +
                                                        CANDIDATES_PATH +
                                                        UTILS_PATH +
                                                        COUNTRIES_PATH,
                                                { response -> cont.resume(JSONObject(response)) },
                                                { volleyError ->
                                                    cont.resumeWithException(volleyError)
                                                }
                                        ) {}
                        )
                    }
            if (response.getBoolean("success")) {
                val personalInfo = deserializeCountries(response)
                Result.success(personalInfo)
            } else {
                val personalInfoError = deserializeCountriesError(response)
                Result.failure(personalInfoError)
            }
        } catch (e: Exception) {
            Log.d("NETWORK_ERROR", e.toString())
            Result.failure(e)
        }
    }

    suspend fun getTechnicalInfo(token: String): Result<TechnicalInfoResponse> {
        return try {
            val response = fetchInfo(token, CANDIDATES_PATH, TECHNICAL_INFO_PATH)
            Log.d("TechnicalInfoResponse", response.toString())
            Result.success(deserializeTechnicalInfo(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTechnicalInfo(
            token: String,
            id: Int
    ): Result<TechnicalInfoItemDeleteResponse> {
        return try {
            val response =
                    suspendCoroutine<JSONObject> { cont ->
                        requestQueue.add(
                                requestWithToken(
                                        token,
                                        Request.Method.DELETE,
                                        CANDIDATES_PATH,
                                        "$TECHNICAL_INFO_PATH/$id",
                                        JSONObject(),
                                        { response -> cont.resume(JSONObject(response)) },
                                        { volleyError ->
                                            if (volleyError.networkResponse != null) {
                                                val errorData =
                                                        String(
                                                                volleyError.networkResponse.data,
                                                                Charsets.UTF_8
                                                        )
                                                val jsonError = JSONObject(errorData)

                                                if (!jsonError.optBoolean("success")) {
                                                    val technicalInfoError =
                                                            deserializeTechnicalInfoItemDeleteError(
                                                                    jsonError
                                                            )
                                                    cont.resumeWithException(technicalInfoError)
                                                }
                                            } else {
                                                cont.resumeWithException(volleyError)
                                            }
                                        }
                                )
                        )
                    }
            if (response.getBoolean("success")) {
                val technicalInfo = deserializeTechnicalInfoItemDelete(response)
                Result.success(technicalInfo)
            } else {
                val technicalInfoError = deserializeTechnicalInfoItemDeleteError(response)
                Result.failure(technicalInfoError)
            }
        } catch (e: Exception) {
            Log.d("NETWORK_ERROR", e.toString())
            Result.failure(e)
        }
    }

    suspend fun postWorkInfo(
            token: String,
            workInfoItem: JSONObject
    ): Result<WorkInfoItemResponse> {
        return try {
            val response =
                    suspendCoroutine<JSONObject> { cont ->
                        requestQueue.add(
                                requestWithToken(
                                        token,
                                        Request.Method.POST,
                                        CANDIDATES_PATH,
                                        WORK_INFO_PATH,
                                        workInfoItem,
                                        { response -> cont.resume(JSONObject(response)) },
                                        { volleyError ->
                                            if (volleyError.networkResponse != null) {
                                                val errorData =
                                                        String(
                                                                volleyError.networkResponse.data,
                                                                Charsets.UTF_8
                                                        )
                                                val jsonError = JSONObject(errorData)

                                                if (!jsonError.optBoolean("success")) {
                                                    val workInfoError =
                                                            deserializeWorkInfoItemError(jsonError)
                                                    cont.resumeWithException(workInfoError)
                                                }
                                            } else {
                                                cont.resumeWithException(volleyError)
                                            }
                                        }
                                )
                        )
                    }
            if (response.getBoolean("success")) {
                val workInfo = deserializeWorkInfoItem(response)
                Result.success(workInfo)
            } else {
                val workInfoError = deserializeWorkInfoItemError(response)
                Result.failure(workInfoError)
            }
        } catch (e: Exception) {
            Log.d("NETWORK_ERROR", e.toString())
            Result.failure(e)
        }
    }

    suspend fun getWorkInfo(token: String): Result<WorkInfoResponse> {
        return try {
            val response = fetchInfo(token, CANDIDATES_PATH, WORK_INFO_PATH)
            Result.success(deserializeWorkInfo(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteWorkInfo(token: String, id: Int): Result<WorkInfoItemDeleteResponse> {
        return try {
            val response =
                    suspendCoroutine<JSONObject> { cont ->
                        requestQueue.add(
                                requestWithToken(
                                        token,
                                        Request.Method.DELETE,
                                        CANDIDATES_PATH,
                                        "$WORK_INFO_PATH/$id",
                                        JSONObject(),
                                        { response -> cont.resume(JSONObject(response)) },
                                        { volleyError ->
                                            if (volleyError.networkResponse != null) {
                                                val errorData =
                                                        String(
                                                                volleyError.networkResponse.data,
                                                                Charsets.UTF_8
                                                        )
                                                val jsonError = JSONObject(errorData)

                                                if (!jsonError.optBoolean("success")) {
                                                    val workInfoError =
                                                            deserializeWorkInfoItemDeleteError(
                                                                    jsonError
                                                            )
                                                    cont.resumeWithException(workInfoError)
                                                }
                                            } else {
                                                cont.resumeWithException(volleyError)
                                            }
                                        }
                                )
                        )
                    }
            if (response.getBoolean("success")) {
                val workInfo = deserializeWorkInfoItemDelete(response)
                Result.success(workInfo)
            } else {
                val workInfoError = deserializeWorkInfoItemDeleteError(response)
                Result.failure(workInfoError)
            }
        } catch (e: Exception) {
            Log.d("NETWORK_ERROR", e.toString())
            Result.failure(e)
        }
    }

    suspend fun getAllExams(token: String): Result<ExamsResponse> {
        return try {
            val response = fetchInfo(token, EVALUATIONS_PATH, EXAMS_PATH)
            Result.success(deserializeExams(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllExamsResults(token: String): Result<ExamsExtendResponse> {
        return try {
            val response = fetchInfo(token, EVALUATIONS_PATH, EXAM_RESULTS_PATH)
            Result.success(deserializeExamsResult(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun postStartExam(token: String, examId: Int): Result<ExamStartResponse> {
        return try {
            val response =
                    suspendCoroutine<JSONObject> { cont ->
                        requestQueue.add(
                                requestWithToken(
                                        token,
                                        Request.Method.POST,
                                        EVALUATIONS_PATH,
                                        "$EXAM_RESULTS_PATH/$examId$EXAM_ACTION_START",
                                        JSONObject(),
                                        { response -> cont.resume(JSONObject(response)) },
                                        { volleyError ->
                                            if (volleyError.networkResponse != null) {
                                                val errorData =
                                                        String(
                                                                volleyError.networkResponse.data,
                                                                Charsets.UTF_8
                                                        )
                                                val jsonError = JSONObject(errorData)

                                                if (!jsonError.optBoolean("success")) {
                                                    val examStartError =
                                                            deserializeExamStartError(jsonError)
                                                    cont.resumeWithException(examStartError)
                                                }
                                            } else {
                                                cont.resumeWithException(volleyError)
                                            }
                                        }
                                )
                        )
                    }
            if (response.getBoolean("success")) {
                val examStart = deserializeExamStart(response)
                Result.success(examStart)
            } else {
                val examStartError = deserializeExamStartError(response)
                Result.failure(examStartError)
            }
        } catch (e: Exception) {
            Log.d("NETWORK_ERROR", e.toString())
            Result.failure(e)
        }
    }

    suspend fun postAnswerQuestion(
            token: String,
            idResult: Int,
            answer: JSONObject
    ): Result<AnswerQuestionResponse> {
        return try {
            val response =
                    suspendCoroutine<JSONObject> { cont ->
                        requestQueue.add(
                                requestWithToken(
                                        token,
                                        Request.Method.POST,
                                        EVALUATIONS_PATH,
                                        "$EXAM_RESULTS_PATH/$idResult$EXAM_ACTION_ANSWER",
                                        answer,
                                        { response -> cont.resume(JSONObject(response)) },
                                        { volleyError ->
                                            if (volleyError.networkResponse != null) {
                                                val errorData =
                                                        String(
                                                                volleyError.networkResponse.data,
                                                                Charsets.UTF_8
                                                        )
                                                val jsonError = JSONObject(errorData)

                                                if (!jsonError.optBoolean("success")) {
                                                    val answerQuestionError =
                                                            deserializeAnswerQuestionError(
                                                                    jsonError
                                                            )
                                                    cont.resumeWithException(answerQuestionError)
                                                }
                                            } else {
                                                cont.resumeWithException(volleyError)
                                            }
                                        }
                                )
                        )
                    }
            if (response.getBoolean("success")) {
                val answerQuestion = deserializeAnswerQuestion(response)
                Result.success(answerQuestion)
            } else {
                val answerQuestionError = deserializeAnswerQuestionError(response)
                Result.failure(answerQuestionError)
            }
        } catch (e: Exception) {
            Log.d("NETWORK_ERROR", e.toString())
            Result.failure(e)
        }
    }

    suspend fun getAllInterviews(token: String): Result<InterviewsResponse> {
        return try {
            val response = fetchInfo(token, CANDIDATES_PATH, INTERVIEWS_PATH)
            Result.success(deserializeInterviews(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllVacancies(token: String): Result<VacanciesResponse> {
        return try {
            val response = fetchInfo(token, COMPANIES_PATH, VACANCY_PATH)
            Result.success(deserializeVacancies(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVacancy(token: String, vacancyId: Int): Result<VacancyResponse> {
        return try {
            val response = fetchInfo(token, COMPANIES_PATH, "$VACANCY_PATH/$vacancyId")
            Result.success(deserializeVacancy(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun postTestResult(
            token: String,
            vacancyId: Int,
            requestJson: JSONArray
    ): Result<VacancyResponse> {
        return try {
            val response =
                    suspendCoroutine<JSONObject> { cont ->
                        requestQueue.add(
                                requestArrayWithToken(
                                        token,
                                        Request.Method.POST,
                                        COMPANIES_PATH,
                                        "$VACANCY_PATH/$vacancyId$TEST_RESULT_PATH",
                                        requestJson,
                                        { response -> cont.resume(JSONObject(response)) },
                                        { volleyError ->
                                            if (volleyError.networkResponse != null) {
                                                val errorData =
                                                        String(
                                                                volleyError.networkResponse.data,
                                                                Charsets.UTF_8
                                                        )
                                                val jsonError = JSONObject(errorData)

                                                if (!jsonError.optBoolean("success")) {
                                                    val testResultError =
                                                            deserializeTechnicalProofError(
                                                                    jsonError
                                                            )
                                                    cont.resumeWithException(testResultError)
                                                }
                                            } else {
                                                cont.resumeWithException(volleyError)
                                            }
                                        }
                                )
                        )
                    }
            if (response.getBoolean("success")) {
                val vacancyResponse = deserializeVacancy(response)
                Result.success(vacancyResponse)
            } else {
                val vacancyError = deserializeVacancyError(response)
                Result.failure(vacancyError)
            }
        } catch (e: Exception) {
            Log.d("NETWORK_ERROR", e.toString())
            Result.failure(e)
        }
    }

    suspend fun getAllTeams(token: String): Result<TeamsResponse> {
        return try {
            val response = fetchInfo(token, COMPANIES_PATH, TEAM_PATH)
            Result.success(deserializeTeams(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun postSelectCandidate(token: String, vacancyId: Int, candidateId: Int): Result<VacancySelectCandidateResponse> {
        return try {
            val response =
                    suspendCoroutine<JSONObject> { cont ->
                        requestQueue.add(
                                requestWithToken(
                                        token,
                                        Request.Method.POST,
                                        COMPANIES_PATH,
                                        "$VACANCY_PATH/$vacancyId$SELECT_CANDIDATE_PATH",
                                        JSONObject().put("id_candidate", candidateId),
                                        { response -> cont.resume(JSONObject(response)) },
                                        { volleyError -> cont.resumeWithException(volleyError) }
                                )
                        )
                    }
            if (response.getBoolean("success")) {
                val vacancyResponse = deserializeVacancySelectCandidateResponse(response)
                Result.success(vacancyResponse)
            } else {
                val vacancyError = deserializeVacancySelectCandidateError(response)
                Result.failure(vacancyError)
            }
        } catch (e: Exception) {
            Log.d("NETWORK_ERROR", e.toString())
            Result.failure(e)
        }
    }

    suspend fun getHiredEmployees(token: String): Result<EmployeesResponse> {
        return try {
            val response = fetchInfo(token, COMPANIES_PATH, HIRED_EMPLOYEES_PATH)
            Result.success(deserializeEmployeesResponse(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun postEvaluateEmployee(token: String, idEmployee: Int, requestBody: EvaluationEmployeeRequest): Result<EmployeeResponse>{
        return try{
            val response =
                suspendCoroutine<JSONObject> { it ->
                    requestQueue.add(
                        requestWithToken(
                            token,
                            Request.Method.POST,
                            COMPANIES_PATH,
                            "$EMPLOYEE_PATH/$idEmployee$EVALUATION_PATH",
                            serializeEvaluationEmployeeRequest(requestBody),
                            {response -> it.resume(JSONObject(response))},
                            {volleyError -> it.resumeWithException(volleyError)}
                        )
                    )
                }
            if(response.getBoolean("success"))
            {
                val employeeResponse = deserializeEmployeeResponse(response)
                Result.success(employeeResponse)
            }
            else
            {
                val employeeResponseError = deserializeEmployeeError(response)
                Result.failure(employeeResponseError)
            }
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }
}
