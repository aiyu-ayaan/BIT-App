package com.atech.core.retrofit

import com.atech.core.retrofit.client.BITApiClient
import com.atech.core.retrofit.client.Holiday
import com.atech.core.retrofit.client.HolidayModel
import com.atech.core.utils.networkFetchData
import javax.inject.Inject

data class ApiCases @Inject constructor(
    val aboutUs: FetchAboutUs,
    val holiday: FetchHoliday,
    val society: FetchSociety,
    val syllabus: FetchSyllabus,
    val syllabusMarkdown: FetchSyllabusMarkdown,
    val course: FetchCourse,
    val administration: FetchAdministration,
    val fetchSyllabusAPI: FetchSyllabusAPI,
    val fetchHolidayApi: FetchHolidayApi
)

class FetchAboutUs @Inject constructor(
    private val api: BITApiClient
) {
    suspend operator fun invoke() = networkFetchData(fetch = {
        api.getAboutUs()
    })
}

class FetchHoliday @Inject constructor(
    private val api: BITApiClient
) {
    suspend operator fun invoke(
        query: String = "all",
        filter: (query: String, HolidayModel) -> List<Holiday> = { q, h ->
            h.holidays.filter { holiday ->
                holiday.type == q
            }
        }
    ) = networkFetchData(
        fetch = {
            api.getHoliday()
        },
        action = { holidays ->
            HolidayModel(filter(query, holidays))
        }
    )
}

class FetchHolidayApi @Inject constructor(
    private val api: BITApiClient
) {
    suspend operator fun invoke(
        query: String = "all",
        filter: (query: String, HolidayModel) -> List<Holiday> = { q, h ->
            h.holidays.filter { holiday ->
                holiday.type == q
            }
        }
    ) = HolidayModel(filter(query, api.getHoliday()))
}

class FetchSociety @Inject constructor(
    private val api: BITApiClient
) {
    suspend operator fun invoke() = networkFetchData(fetch = {
        api.getSociety()
    })
}

class FetchSyllabus @Inject constructor(
    private val api: BITApiClient
) {
    suspend operator fun invoke(id: String) = networkFetchData(fetch = {
        api.getSubjects(
            course = id.replace("\\d".toRegex(), ""),
            courseYear = id
        )
    })
}

class FetchSyllabusMarkdown @Inject constructor(
    private val api: BITApiClient
) {
    suspend operator fun invoke(
        course: String,
        courseSem: String,
        subject: String
    ) = networkFetchData(fetch = {
        api.getSubjectMarkdown(course, courseSem, subject)
    })
}

class FetchCourse @Inject constructor(
    private val api: BITApiClient
) {
    suspend operator fun invoke() = networkFetchData(fetch = {
        api.getCourse()
    })
}

class FetchAdministration @Inject constructor(
    private val api: BITApiClient
) {
    suspend operator fun invoke() = networkFetchData(fetch = {
        api.getAdministration()
    })
}

class FetchSyllabusAPI @Inject constructor(
    private val api: BITApiClient
) {
    suspend operator fun invoke(id: String) = api.getSubjects(
        course = id.replace("\\d".toRegex(), ""),
        courseYear = id
    )
}