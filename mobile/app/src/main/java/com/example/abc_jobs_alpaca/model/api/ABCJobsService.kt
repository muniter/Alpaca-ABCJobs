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


    suspend fun postCandidate(newCandidate: JSONObject) = suspendCoroutine<Candidate> { cont ->
        requestQueue.add(
            postRequest("", newCandidate, { response ->
                val candidate = deserializeCandidate(JSONObject(response))
                cont.resume(candidate)
            }, {
                if (it.networkResponse != null) {
                    Log.d("NetErr", it.networkResponse.toString())
                } else {
                    Log.d("NetErr", "NetworkResponse es nulo")
                }
                cont.resumeWithException(it)
            })
        )
    }

    private fun postRequest(
        path: String,
        body: JSONObject,
        responseListener: Response.Listener<String>,
        errorListener: Response.ErrorListener,
    ): StringRequest {
        return object : StringRequest(
            Method.POST, "https://api.abc.muniter.link/candidatos/crear", responseListener, errorListener
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