package com.example.abc_jobs_alpaca


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository

class WelcomeActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        var abcJobsRepository = ABCJobsRepository(this.application)
    }



}