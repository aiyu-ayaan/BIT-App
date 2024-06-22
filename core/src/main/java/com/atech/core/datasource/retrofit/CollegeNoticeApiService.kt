/*
 *  Created by Aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.core.datasource.retrofit

import retrofit2.http.GET

interface CollegeNoticeApiService {
    companion object {
        const val COLLEGE_BASE_URL = "https://www.bitmesra.ac.in/"
    }

    @GET("Display_Archive_News_List09398FGDr?cid=7")
    suspend fun getNotices(): String
}