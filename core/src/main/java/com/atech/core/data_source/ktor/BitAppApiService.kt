package com.atech.core.data_source.ktor

import com.atech.core.data_source.ktor.model.SyllabusResponse

interface BitAppApiService {
    companion object {
        const val BASE_URL = "https://bit-lalpur-app.github.io/BIT-App-Data/data/"
    }

    suspend fun getSubjects(course: String, courseSem: String): SyllabusResponse
}