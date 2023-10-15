package com.example.abc_jobs_alpaca.model.repository


import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import com.example.abc_jobs_alpaca.model.api.ABCJobsService
import com.example.abc_jobs_alpaca.model.models.*

class ABCJobsRepository(private val applicationContext: Application) {

    @SuppressLint("SuspiciousIndentation")
    suspend fun postCandidate(newCandidate: Candidate): Candidate {
        val candidateJson = serializeCandidate(newCandidate)
            return ABCJobsService.getInstance(applicationContext).postCandidate(candidateJson)
    }
}