package com.example.abc_jobs_alpaca.model.models
data class ShortlistedCandidateItem (
    var idCandidate: Int,
    var idPersona: Int,
    var names: String,
    var lastNames: String,
    var fullName: String,
    var email: String,
    var birthDate: String,
    var countryCode: Int,
    var country: String,
    var city: String,
    var address: String,
    var phone: String,
    var biography: String,
    var languages: List<Language>,
    var result: Int
) {
    var resultString = result.toString()
}

