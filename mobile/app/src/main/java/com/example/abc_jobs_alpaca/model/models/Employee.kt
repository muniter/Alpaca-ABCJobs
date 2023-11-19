package com.example.abc_jobs_alpaca.model.models

import com.google.gson.annotations.SerializedName

data class Employee(
    val id: Int,
    @SerializedName("id_persona") val idPersona: Int,
    val name: String,
    val title: String,
    val company: CompanyDetails,
    val personality: Personality,
    val skills: List<SkillInfoType>,
    val evaluations: List<Any>,
    val teams: List<Team>
)