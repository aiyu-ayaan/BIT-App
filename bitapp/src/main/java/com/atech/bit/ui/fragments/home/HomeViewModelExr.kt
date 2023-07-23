package com.atech.bit.ui.fragments.home

import androidx.annotation.Keep
import com.atech.bit.ui.fragments.home.adapter.HomeItems
import com.atech.bit.utils.HomeTopModel
import com.atech.core.retrofit.ApiCases
import com.atech.core.retrofit.client.Holiday
import com.atech.core.retrofit.client.HolidayModel
import com.atech.core.room.attendance.AttendanceModel
import com.atech.core.room.library.LibraryModel
import com.atech.core.room.syllabus.SyllabusDao
import com.atech.core.utils.getData
import com.atech.course.sem.adapter.OfflineSyllabusUIMapper
import com.atech.course.sem.adapter.OnlineSyllabusUIMapper
import com.atech.course.sem.adapter.SyllabusUIModel
import com.atech.theme.CardHighlightModel
import com.atech.theme.R
import com.atech.theme.compareDifferenceInDays
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import java.util.Date

object HomeViewModelExr {

    //    ____________________________________________ Syllabus ____________________________________________
    fun topView(
        list: MutableList<HomeItems>,
        library: List<LibraryModel>,
        isOnline: Boolean,
        isPermissionGranted: Boolean
    ) {
        if (!isPermissionGranted)
            list.add(
                HomeItems.Highlight(
                    CardHighlightModel(
                        "Notification is disabled",
                        "Allow Notification to get latest notice and announcement",
                        R.drawable.ic_notice
                    )
                )
            )

        library.filter3Day().also {
            if (it.isNotEmpty()) {
                list.add(HomeItems.Title("Library"))
                list.add(HomeItems.Library(it))
            }
        }
        list.add(
            HomeItems.Settings(
                HomeTopModel(
                    title = "Your Subjects",
                    isOnline = isOnline
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

    //___________________________________________ Library ________________________________________________
    private fun List<LibraryModel>.filter3Day() = filter { book ->
        val diff = Date(book.returnDate).compareDifferenceInDays(
            Date(System.currentTimeMillis())
        )
        diff in 0..3 && !book.markAsReturn
    }

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