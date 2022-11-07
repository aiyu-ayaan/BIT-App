package com.atech.core.api

import com.atech.core.api.aboutus.AboutUsModel
import com.atech.core.api.holiday.HolidayModel
import com.atech.core.api.society.SocietyModel
import com.atech.core.api.syllabus.SyllabusResponse
import retrofit2.http.GET
import retrofit2.http.Path


interface BITApiClient {

    companion object {
        const val BASE_URL = "https://aiyu-ayaan.github.io/BIT-App-Data/"
    }

    @GET("data/syllabus/{course}/{course_sem}.json")
    suspend fun getSubjects(@Path("course")course : String,@Path("course_sem") courseYear: String): SyllabusResponse


    @GET("data/aboutUs/aboutUs.json")
    suspend fun getAboutUs(): AboutUsModel

    @GET("data/holiday/holiday.json")
    suspend fun getHoliday(): HolidayModel

    @GET("data/society/society.json")
    suspend fun getSociety(): SocietyModel

}