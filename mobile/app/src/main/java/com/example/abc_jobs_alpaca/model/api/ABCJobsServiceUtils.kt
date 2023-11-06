package com.example.abc_jobs_alpaca.model.api

import android.content.Context
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.abc_jobs_alpaca.model.models.AcademicInfoTypeResponse
import com.example.abc_jobs_alpaca.model.models.SkillInfoTypeResponse
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoTypeResponse
import com.example.abc_jobs_alpaca.model.models.deserializeSkillInfoTypes
import com.example.abc_jobs_alpaca.model.models.deserializeSkillInfoTypesError
import com.example.abc_jobs_alpaca.model.models.deserializeTechnicalInfoTypes
import com.example.abc_jobs_alpaca.model.models.deserializeTechnicalInfoTypesError
import com.example.abc_jobs_alpaca.model.models.deserializeTypesTitles
import com.example.abc_jobs_alpaca.model.models.deserializeTypesTitlesError
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class ABCJobsServiceUtils constructor(context: Context) {

    companion object{
        private var BASEURL = "https://api.abc.muniter.link"
        private var CANDIDATES_PATH = "/candidatos"
        private var UTILS_PATH = "/utils"
        private var TITLE_TYPES_PATH = "/title-types"
        private var SKILLS_PATH = "/skills"
        private var TECHNICAL_TYPES_PATH = "/technical-info-types"
        private var instance: ABCJobsServiceUtils? = null

        fun getInstance(context: Context) = instance ?: synchronized(this){
            instance ?: ABCJobsServiceUtils(context).also {
                instance = it
            }
        }
    }
    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    suspend fun getTypesTitle(token: String): Result<AcademicInfoTypeResponse>{
        return try {
            val response = suspendCoroutine<JSONObject> { cont ->
                requestQueue.add(
                    object : StringRequest(
                        Method.GET, BASEURL + CANDIDATES_PATH + UTILS_PATH + TITLE_TYPES_PATH,
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

    suspend fun getTechnicalInfoTypes(token: String): Result<TechnicalInfoTypeResponse>{
        return try {
            val response = suspendCoroutine<JSONObject> { cont ->
                requestQueue.add(
                    object : StringRequest(
                        Method.GET, BASEURL + CANDIDATES_PATH + UTILS_PATH + TECHNICAL_TYPES_PATH,
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
            if (response.getBoolean("success")) {
                val technicalTypes = deserializeTechnicalInfoTypes(response)
                Result.success(technicalTypes)
            } else {
                val technicalTypesError = deserializeTechnicalInfoTypesError(response)
                Result.failure(technicalTypesError)
            }
        } catch (e: Exception) {
            Log.d("NETWORK_ERROR", e.toString())
            Result.failure(e)
        }
    }


    suspend fun getSkillsTypes(token: String): Result<SkillInfoTypeResponse>{
        return try {
            val response = suspendCoroutine<JSONObject> { cont ->
                requestQueue.add(
                    object : StringRequest(
                        Method.GET, BASEURL + CANDIDATES_PATH + UTILS_PATH + SKILLS_PATH,
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
            if (response.getBoolean("success")) {
                val typesTitle = deserializeSkillInfoTypes(response)
                Result.success(typesTitle)
            } else {
                val typesTitleError = deserializeSkillInfoTypesError(response)
                Result.failure(typesTitleError)
            }
        } catch (e: Exception) {
            Log.d("NETWORK_ERROR", e.toString())
            Result.failure(e)
        }
    }

}