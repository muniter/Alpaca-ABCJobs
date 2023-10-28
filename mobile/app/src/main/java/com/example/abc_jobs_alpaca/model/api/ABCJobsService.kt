package com.example.abc_jobs_alpaca.model.api


import android.content.Context
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.abc_jobs_alpaca.model.models.*
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
class ABCJobsService constructor(context: Context){

    companion object{
        private var BASEURL = "https://api.abc.muniter.link/"
        private var CANDIDATES_PATH = "candidatos"
        private var USERS_PATH = "usuarios"
        private var CREATE_PATH = "/crear"
        private var LOGIN_PATH = "/login"
        private var CONFIG_PATH = "/config"
        private var instance: ABCJobsService? = null

        fun getInstance(context: Context) = instance ?: synchronized(this){
            instance ?: ABCJobsService(context).also {
                instance = it
            }
        }
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    private fun postRequest(
        path: String,
        action: String,
        body: JSONObject,
        responseListener: Response.Listener<String>,
        errorListener: Response.ErrorListener,
    ): StringRequest {
        return object : StringRequest(
            Method.POST, BASEURL+path+action, responseListener, errorListener
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
    suspend fun postCandidate(newCandidate: JSONObject): Result<UserRegisterResponse> {
        return try {
            val response = suspendCoroutine<JSONObject> { cont ->
                requestQueue.add(
                    postRequest(CANDIDATES_PATH, CREATE_PATH, newCandidate, { response ->
                        cont.resume(JSONObject(response))
                    }, { volleyError ->
                        if (volleyError.networkResponse != null) {
                            val errorData = String(volleyError.networkResponse.data, Charsets.UTF_8)
                            val jsonError = JSONObject(errorData)

                            if (!jsonError.optBoolean("success")){
                                val candidateError = deserializeCandidateError(jsonError)
                                cont.resumeWithException(candidateError)
                            }
                        } else {
                            cont.resumeWithException(volleyError)
                        }
                    })
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
            val response = suspendCoroutine<JSONObject> { cont ->
                requestQueue.add(
                    postRequest(USERS_PATH, LOGIN_PATH, loginUserJson, { response ->
                        cont.resume(JSONObject(response))
                    }, { volleyError ->
                        if (volleyError.networkResponse != null) {
                            val errorData = String(volleyError.networkResponse.data, Charsets.UTF_8)
                            val jsonError = JSONObject(errorData)

                            if (!jsonError.optBoolean("success")) {
                                val userLoginResponseError = deserializeLoginCandidateError(jsonError)
                                cont.resumeWithException(userLoginResponseError)
                            }
                        } else {
                            cont.resumeWithException(volleyError)
                        }
                    })
                )
            }
            if (response.getBoolean("success")) {
                val userLoginResponse = deserializeLoginCandidate(response)
                Result.success(userLoginResponse)
            } else {
                val userLoginResponseError = deserializeLoginCandidateError(response)
                Result.failure(userLoginResponseError);
            }
        } catch (e: Exception) {
            Log.d("NETWORK_ERROR", e.toString())
            Result.failure(e)
        }
    }

    suspend fun getConfig(token: String): Result<ConfigData> {

        if (token.isEmpty()) {
            // Manejar el caso en el que el token esté vacío
            Log.d("NETWORK_ERROR", "El token está vacío")
            return Result.failure(Exception("El token está vacío"))
        }

        return try {
            val response = fetchConfig(token)
            Log.d("MiTag getConfig", response.toString())
            Result.success(handleConfigResponse(response).getOrThrow())
        } catch (e: Exception) {
            // Manejar excepciones aquí
            Result.failure(e)
        }
    }

    suspend fun fetchConfig(token: String): JSONObject {
        return suspendCoroutine { cont ->
            requestQueue.add(
                object : StringRequest(
                    Method.GET, BASEURL + USERS_PATH + CONFIG_PATH,
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

    suspend fun postConfig(token: String, configJson: JSONObject): Result<ConfigData> {
        return try {
            val response = suspendCoroutine { cont ->
                requestQueue.add(
                    object : StringRequest(
                        Method.POST, BASEURL + USERS_PATH + CONFIG_PATH,
                        { response -> cont.resume(JSONObject(response)) },
                        { volleyError -> cont.resumeWithException(volleyError) }
                    ) {
                        override fun getBodyContentType(): String {
                            return "application/json; charset=utf-8"
                        }

                        override fun getBody(): ByteArray {
                            Log.d("Sending body", configJson.toString())
                            return configJson.toString().toByteArray()
                        }

                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Authorization"] = "Bearer $token"
                            return headers
                        }
                    }
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

                    val languageAppStringMap = UserLanguageApp.values().find { it.name == languageAppString }
                    val timeFormatStringMap = UserTimeFormat.values().find { it.formatString == timeFormatString }
                    val dateFormatStringMap = UserDateFormat.values().find { it.formatString == dateFormatString }

                    val configData = ConfigData(languageAppStringMap!!, timeFormatStringMap!!, dateFormatStringMap!!)

                    return Result.success(configData)
                }
            }
            return Result.failure(Exception("Error en la respuesta del servidor"))
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }


}