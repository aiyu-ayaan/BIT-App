package com.atech.core.api

import com.atech.core.api.model.SyllabusResponse
import retrofit2.http.GET


interface SyllabusApi {

    companion object {
        const val BASE_URL = "https://aiyu-ayaan.github.io/BIT-App-Syllabus/data/"
    }

    @GET("bca.json")
    suspend fun getSubjects(): SyllabusResponse

    
}