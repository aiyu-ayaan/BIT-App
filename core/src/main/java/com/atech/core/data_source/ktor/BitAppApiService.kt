package com.atech.core.data_source.ktor

import com.atech.core.data_source.ktor.model.SyllabusResponse

interface BitAppApiService {

    suspend fun getSubjects(course: String, courseSem: String): SyllabusResponse

    suspend fun getSubjectMarkDown(course: String, courseSem: String, subject: String): String
}