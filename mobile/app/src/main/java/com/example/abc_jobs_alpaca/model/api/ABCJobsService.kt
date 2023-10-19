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
        private var CREATE_PATH = "/crear"
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


    suspend fun postCandidate(newCandidate: JSONObject): Result<Candidate> {
        return try {
            val response = suspendCoroutine<JSONObject> { cont ->
                requestQueue.add(
                    postRequest(CANDIDATES_PATH, CREATE_PATH, newCandidate, { response ->
                        cont.resume(JSONObject(response))
                    }, {
                        if (it.networkResponse != null) {
                            Log.d("NetErr", it.networkResponse.toString())
                        } else {
                            Log.d("NetErr", "NetworkResponse is null")
                        }
                        cont.resumeWithException(it)
                    })
                )
            }

            if (response.optBoolean("success", false)) {
                val candidate = deserializeCandidate(response)
                Result.success(candidate)
            } else {
                Result.failure(Exception("Request failed"))
            }
        } catch (e: Exception) {
            Log.d("NETWORK_ERROR", e.toString())
            Result.failure(e)
        }
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


}