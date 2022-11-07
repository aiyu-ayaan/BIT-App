package com.atech.core.api

import com.atech.core.api.holiday.Holiday
import com.atech.core.api.holiday.HolidayModel
import com.atech.core.api.syllabus.SyllabusResponse
import com.atech.core.utils.DataState
import com.atech.core.utils.networkFetchData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ApiRepository @Inject constructor(
    private val api: BITApiClient
) {
    

    fun getSyllabus(id: String): Flow<DataState<SyllabusResponse>> = networkFetchData(
        fetch = {
            api.getSubjects(
                course = id.replace("\\d".toRegex(), ""),
                courseYear = id
            )
        }
    )

    fun getAboutUsData() = networkFetchData(
        fetch = {
            api.getAboutUs()
        }
    )

    fun getHolidayData(
        query: String = "all", filter: (query: String, HolidayModel) -> List<Holiday> = { q, h ->
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

    fun getSocietyData() = networkFetchData(
        fetch = {
            api.getSociety()
        }
    )

}