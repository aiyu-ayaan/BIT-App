/*
 *  Created by Aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.core.usecase

import android.util.Log
import com.atech.core.datasource.retrofit.CollegeNoticeApiService
import com.atech.core.datasource.retrofit.CollegeNoticeApiService.Companion.COLLEGE_BASE_URL
import com.atech.core.datasource.retrofit.model.CollegeNotice
import com.atech.core.utils.TAGS
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import javax.inject.Inject

data class CollegeNoticeUseCases @Inject constructor(
    val allNotice: GetAllNotice
)

data class GetAllNotice @Inject constructor(
    val api: CollegeNoticeApiService
) {
    suspend fun invoke() = try {
        getNotification(api.getNotices())
    } catch (e: Exception) {
        Log.e(TAGS.BIT_ERROR.name, "invoke: $e")
        emptyList()
    }

    private fun getNotification(content: String): List<CollegeNotice> =
        Jsoup.parse(content)
            .getElementsByClass("blog-list-post clearfix news-item")
            .map(::extractNotice)
            .filter { it.title.isNotBlank() }


    private fun extractNotice(ele: Element): CollegeNotice {
        val title = ele.getElementsByTag("a").firstOrNull()?.text() ?: ""
        val link = ele.getElementsByTag("a").attr("onclick").replace(
            "return makePopUp('/", ""
        ).replace(
            """.*?(UploadedDocuments/.*?\.pdf).*""".toRegex(), "$1"
        )
        val date = ele.getElementsByTag("span").firstOrNull()?.text() ?: ""
        return CollegeNotice(
            title = title, date = date, link = COLLEGE_BASE_URL + link
        )
    }
}