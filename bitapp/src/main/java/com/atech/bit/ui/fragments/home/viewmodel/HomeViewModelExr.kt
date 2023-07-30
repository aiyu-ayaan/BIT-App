package com.atech.bit.ui.fragments.home.viewmodel

import androidx.annotation.Keep
import com.atech.bit.ui.fragments.home.adapter.HomeItems
import com.atech.core.retrofit.ApiCases
import com.atech.core.retrofit.client.Holiday
import com.atech.core.retrofit.client.HolidayModel
import com.atech.core.room.attendance.AttendanceModel
import com.atech.core.room.syllabus.SyllabusDao
import com.atech.core.utils.getData
import com.atech.course.sem.adapter.OfflineSyllabusUIMapper
import com.atech.course.sem.adapter.SyllabusUIModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

object HomeViewModelExr {
    private fun Triple<List<SyllabusUIModel>, List<SyllabusUIModel>, List<SyllabusUIModel>>.mapToHomeItems(): List<HomeItems> {
        val list = mutableListOf<HomeItems>()
        if (first.isNotEmpty()) {
            list.add(HomeItems.Title("Theory"))
            list.addAll(first.map { HomeItems.Subject(it) })
        }
        if (second.isNotEmpty()) {
            list.add(HomeItems.Title("Lab"))
            list.addAll(second.map { HomeItems.Subject(it) })
        }
        if (third.isNotEmpty()) {
            list.add(HomeItems.Title("PE"))
            list.addAll(third.map { HomeItems.Subject(it) })
        }
        return list
    }

//    ____________________________________________ Holiday ____________________________________________

    suspend fun getHoliday(
        api: ApiCases,
        query: String,
        filter: (query: String, HolidayModel) -> List<Holiday> = { q, h ->
            h.holidays.filter { holiday ->
                holiday.month == q
            }
        }
    ) = withContext(Dispatchers.IO) {
        api.holiday.invoke(query, filter).map { dataState ->
            dataState.getData()?.let { holiday ->
                holiday.holidays.map { HomeItems.Holiday(it) }
            } ?: emptyList()
        }.toList().flatten()
    }

//    ____________________________________________ Event ____________________________________________

    @Keep
    data class EventHomeModel(
        val title: String,
        val des: String,
        val society: String,
        val iconLink: String,
        val posterLink: String?,
        val path: String,
        val created: Long
    )

    @Keep
    data class AttendanceHomeModel(
        val total: Int,
        val present: Int,
        val data: List<AttendanceModel>
    )


    //    _______________________________________ Search _____________________________________________________
    suspend fun offlineDataSourceSearch(
        syllabusDao: SyllabusDao,
        offlineSyllabusUIMapper: OfflineSyllabusUIMapper,
        courseSem: String
    ) =
        Triple(
            offlineSyllabusUIMapper.mapFromEntityList(
                syllabusDao.getSyllabusSearchSync(
                    courseSem,
                    "Theory"
                )
            ),
            offlineSyllabusUIMapper.mapFromEntityList(
                syllabusDao.getSyllabusSearchSync(
                    courseSem,
                    "Lab"
                )
            ),
            offlineSyllabusUIMapper.mapFromEntityList(
                syllabusDao.getSyllabusSearchSync(
                    courseSem,
                    "PE"
                )
            )
        ).mapToHomeItems()

}