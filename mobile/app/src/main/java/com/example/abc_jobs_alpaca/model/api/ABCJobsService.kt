package com.example.abc_jobs_alpaca.model.api


import android.content.Context
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.abc_jobs_alpaca.model.models.*
import com.example.abc_jobs_alpaca.viewmodel.LoginMoldel
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
                var userLoginResponseError = deserializeLoginCandidateError(response)
                Result.failure(userLoginResponseError);
            }
        } catch (e: Exception) {
            Log.d("NETWORK_ERROR", e.toString())
            Result.failure(e)
        }
    }



}