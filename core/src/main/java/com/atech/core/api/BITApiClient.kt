package com.atech.core.api

import com.atech.core.api.aboutus.AboutUsModel
import com.atech.core.api.holiday.HolidayModel
import com.atech.core.api.society.SocietyModel
import com.atech.core.api.syllabus.model.SyllabusResponse
import retrofit2.http.GET
import retrofit2.http.Path


interface BITApiClient {

    companion object {
        const val BASE_URL = "https://aiyu-ayaan.github.io/BIT-App-Data/"
    }

    @GET("syllabus/data/{sem_year}.json")
    suspend fun getSubjects(@Path("sem_year") semYear: String): SyllabusResponse


    @GET("data/aboutUs/aboutUs.json")
    suspend fun getAboutUs(): AboutUsModel

    @GET("data/holiday/holiday.json")
    suspend fun getHoliday(): HolidayModel

    @GET("data/society/society.json")
    suspend fun getSociety(): SocietyModel

}