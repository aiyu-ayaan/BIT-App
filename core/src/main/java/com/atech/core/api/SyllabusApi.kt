package com.atech.core.api

import com.atech.core.api.model.SyllabusResponse
import retrofit2.http.GET
import retrofit2.http.Path


interface SyllabusApi {

    companion object {
        const val BASE_URL = "https://aiyu-ayaan.github.io/BIT-App-Syllabus/"
    }

    @GET("data/{sem_year}.json")
    suspend fun getSubjects(@Path("sem_year") semYear: String): SyllabusResponse


}