package com.atech.core.datasource.retrofit

import com.atech.core.datasource.retrofit.model.HolidayModel
import com.atech.core.datasource.retrofit.model.SocietyModel
import com.atech.core.datasource.retrofit.model.SyllabusResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface BitAppApiService {

    companion object {
        const val BASE_URL = "https://bit-lalpur-app.github.io/BIT-App-Data/data/"
    }

    @GET("syllabus/{course}/{course_sem}/{course_sem}.json")
    suspend fun getSubjects(
        @Path("course") course: String,
        @Path("course_sem") courseSem: String
    ): SyllabusResponse

    @GET("syllabus/{course}/{course_sem}/subjects/{subject}.md")
    suspend fun getSubjectMarkDown(
        @Path("course") course: String,
        @Path("course_sem") courseSem: String,
        @Path("subject") subject: String
    ): String

    @GET("holiday/holiday.json")
    suspend fun getHoliday(): HolidayModel

    @GET("society/society.json")
    suspend fun getSociety(): SocietyModel
}