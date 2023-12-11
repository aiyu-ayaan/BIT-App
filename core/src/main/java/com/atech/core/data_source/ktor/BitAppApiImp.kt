package com.atech.core.data_source.ktor

import com.atech.core.data_source.ktor.model.SyllabusResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url

class BitAppApiImp(
    private val client: HttpClient
) : BitAppApiService {
    @Throws(Exception::class)
    override suspend fun getSubjects(course: String, courseSem: String): SyllabusResponse =
        try {
            client.get {
                url(BITAppApiRoute.Subject(course, courseSem).route)
            }
        } catch (e: Exception) {
            throw e
        }

    @Throws(Exception::class)
    override suspend fun getSubjectMarkDown(
        course: String,
        courseSem: String,
        subject: String
    ): String =
        try {
            client.get {
                url(BITAppApiRoute.SubjectMarkdown(course, courseSem, subject).route)
            }
        } catch (e: Exception) {
            throw e
        }


}