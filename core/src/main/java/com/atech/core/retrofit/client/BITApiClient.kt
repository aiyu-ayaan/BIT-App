package com.atech.core.retrofit.client

import retrofit2.http.GET
import retrofit2.http.Path


interface BITApiClient {

    companion object {
        const val BASE_URL = "https://bit-lalpur-app.github.io/BIT-App-Data/data/"
    }


    @GET("aboutUs/aboutUs.json")
    suspend fun getAboutUs(): AboutUsModel

    @GET("holiday/holiday.json")
    suspend fun getHoliday(): HolidayModel

    @GET("society/society.json")
    suspend fun getSociety(): SocietyModel

    @GET("syllabus/{course}/{course_sem}/{course_sem}.json")
    suspend fun getSubjects(
        @Path("course") course: String,
        @Path("course_sem") courseYear: String
    ): SyllabusResponse

    @GET("syllabus/{course}/{course_sem}/subjects/{subject}.md")
    suspend fun getSubjectMarkdown(
        @Path("course") course: String,
        @Path("course_sem") courseSem: String,
        @Path("subject") subject: String
    ): String

    @GET("syllabus/course.json")
    suspend fun getCourse(): List<CourseDetail>

    @GET("admin/admin.md")
    suspend fun getAdministration(): String

}