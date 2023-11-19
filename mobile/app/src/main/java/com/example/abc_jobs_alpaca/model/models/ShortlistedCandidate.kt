package com.example.abc_jobs_alpaca.model.models
data class ShortlistedCandidateItem(
    val idCandidate: Int,
    val idPersona: Int,
    val names: String,
    val lastNames: String,
    val fullName: String,
    val email: String,
    val birthDate: String,
    val countryCode: Int,
    val country: String,
    val city: String,
    val address: String,
    val phone: String,
    val biography: String,
    val languages: List<Language>,
    val result: Int
)

