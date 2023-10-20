package com.example.abc_jobs_alpaca.model.repository


import android.annotation.SuppressLint
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
}