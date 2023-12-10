package com.atech.core.data_source.ktor

import com.atech.core.data_source.ktor.BitAppApiService.Companion.BASE_URL
import com.atech.core.data_source.ktor.model.SyllabusResponse
import io.ktor.client.HttpClient
import io.ktor.client.features.RedirectResponseException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url

class BitAppApiImp(
    private val client: HttpClient
) : BitAppApiService {
    @Throws(Exception::class)
    override suspend fun getSubjects(course: String, courseSem: String): SyllabusResponse =
        try {
            client.get {
                url("$BASE_URL/syllabus/$course/$courseSem/$courseSem.json")
            }
        } catch (e: Exception) {
            throw e
        }


}