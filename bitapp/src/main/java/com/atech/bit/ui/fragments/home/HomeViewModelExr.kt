package com.atech.bit.ui.fragments.home

import androidx.annotation.Keep
import com.atech.bit.ui.fragments.home.adapter.HomeItems
import com.atech.bit.utils.HomeTopModel
import com.atech.core.retrofit.ApiCases
import com.atech.core.room.attendance.AttendanceModel
import com.atech.core.room.syllabus.SyllabusDao
import com.atech.core.utils.getData
import com.atech.course.sem.adapter.OfflineSyllabusUIMapper
import com.atech.course.sem.adapter.OnlineSyllabusUIMapper
import com.atech.course.sem.adapter.SyllabusUIModel
import com.atech.theme.CardHighlightModel
import com.atech.theme.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

object HomeViewModelExr {

    //    ____________________________________________ Syllabus ____________________________________________
    fun topView(list: MutableList<HomeItems>) {
        list.add(
            HomeItems.Highlight(
                CardHighlightModel(
                    "Notification is disabled",
                    "Allow Notification to get latest notice and announcement",
                    R.drawable.ic_notice
                )
            )
        )
        list.add(
            HomeItems.Settings(
                HomeTopModel(
                    title = "Your Subjects",
                )
            )
        )
    }

    suspend fun offlineDataSource(
        syllabusDao: SyllabusDao,
        offlineSyllabusUIMapper: OfflineSyllabusUIMapper,
        courseSem: String
    ) =
        Triple(
            offlineSyllabusUIMapper.mapFromEntityList(
                syllabusDao.getSyllabusHomeList(
                    courseSem,
                    "Theory"
                )
            ),
            offlineSyllabusUIMapper.mapFromEntityList(
                syllabusDao.getSyllabusHomeList(
                    courseSem,
                    "Lab"
                )
            ),
            offlineSyllabusUIMapper.mapFromEntityList(
                syllabusDao.getSyllabusHomeList(
                    courseSem,
                    "PE"
                )
            )
        ).mapToHomeItems()

    suspend fun onlineDataSource(
        api: ApiCases,
        onlineSyllabusUIMapper: OnlineSyllabusUIMapper,
        courseSem: String
    ) =
        api.syllabus.invoke(courseSem.lowercase()).map { dataStore ->
            (dataStore.getData()?.let { syllabus ->
                Triple(
                    onlineSyllabusUIMapper.mapFromEntityList(
                        (syllabus.semester?.subjects?.theory) ?: emptyList()
                    ),
                    onlineSyllabusUIMapper.mapFromEntityList(
                        (syllabus.semester?.subjects?.lab) ?: emptyList()
                    ),
                    onlineSyllabusUIMapper.mapFromEntityList(
                        (syllabus.semester?.subjects?.pe) ?: emptyList()
                    )
                )
            } ?: emptyTriple()).mapToHomeItems()
        }.toList().flatten()


    val devNote = HomeItems.DevNote
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

    private fun emptyTriple(): Triple<List<SyllabusUIModel>, List<SyllabusUIModel>, List<SyllabusUIModel>> =
        Triple(emptyList(), emptyList(), emptyList())

//    ____________________________________________ Holiday ____________________________________________

    suspend fun getHoliday(
        api: ApiCases,
        month: String
    ) = withContext(Dispatchers.IO) {
        api.holiday.invoke(month, filter = { query, holidays ->
            holidays.holidays.filter { it.month == query }
        }).map { dataState ->
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


}