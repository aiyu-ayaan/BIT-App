package com.atech.bit.ui.fragments.home.util

import com.atech.bit.ui.fragments.home.FilterPreferences
import com.atech.bit.ui.fragments.home.HomeViewModelExr
import com.atech.bit.ui.fragments.home.adapter.HomeItems
import com.atech.bit.utils.HomeTopModel
import com.atech.core.firebase.firestore.EventModel
import com.atech.core.firebase.firestore.FirebaseCases
import com.atech.core.retrofit.ApiCases
import com.atech.core.retrofit.client.Holiday
import com.atech.core.retrofit.client.HolidayModel
import com.atech.core.room.library.LibraryModel
import com.atech.core.room.syllabus.SyllabusDao
import com.atech.course.sem.adapter.OfflineSyllabusUIMapper
import com.atech.course.sem.adapter.OnlineSyllabusUIMapper
import com.atech.course.sem.adapter.SyllabusUIModel
import com.atech.theme.CardHighlightModel
import com.atech.theme.R
import com.atech.theme.compareDifferenceInDays
import com.atech.theme.isAPI33AndUp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.coroutines.suspendCoroutine

class GetHomeData(
    private val filterPreferences: FilterPreferences,
) {

    fun getHomeItems() = channelFlow<List<HomeItems>> {
        val list = mutableListOf<HomeItems>()
        notificationAccess(list)
        getLibraryData(filterPreferences.library).also { list.addAll(it) }
        list.add(getTopSetting(filterPreferences.isOnline))
        getSyllabus().also { list.addAll(it) }
        getHoliday().also { list.addAll(it) }
        getEvent()?.also {
            list.addAll(it)
        }
        getCGPA().also { list.addAll(it) }
        getAttendance().also { list.addAll(it) }
        list.add(HomeItems.DevNote)
        send(list)
        awaitClose()
    }.flowOn(Dispatchers.IO)

    private fun notificationAccess(list: MutableList<HomeItems>) {
        isAPI33AndUp {
            if (!filterPreferences.isPermissionGranted) list.add(
                HomeItems.Highlight(
                    CardHighlightModel(
                        "Notification is disabled",
                        "Allow Notification to get latest notice and announcement",
                        R.drawable.ic_notice
                    )
                )
            )
        }
    }

    private suspend fun getLibraryData(
        library: List<LibraryModel>
    ) = coroutineScope {
        withContext(Dispatchers.IO) {
            val d = async {
                val list = mutableListOf<HomeItems>()
                library.filter3Day().also {
                    if (it.isNotEmpty()) {
                        list.add(HomeItems.Title("Library"))
                        list.add(HomeItems.Library(it))
                    }
                }
                list
            }
            d.await()
        }
    }

    private fun getTopSetting(
        isOnline: Boolean
    ) = HomeItems.Settings(
        HomeTopModel(
            title = "Your Subjects", isOnline = isOnline
        )
    )

    private suspend fun getSyllabus() = (if (!filterPreferences.isOnline) getOfflineData(
        filterPreferences.courseWithSem,
        filterPreferences.syllabusDao,
        filterPreferences.offlineSyllabusUIMapper
    )
    else getOnlineSyllabusData(
        filterPreferences.courseWithSem,
        filterPreferences.api,
        filterPreferences.onlineSyllabusUIMapper
    )).let {
        it.ifEmpty { listOf(HomeItems.NoData) }
    }


    private suspend fun getOnlineSyllabusData(
        courseSem: String, apiCases: ApiCases, onlineSyllabusUIMapper: OnlineSyllabusUIMapper
    ): List<HomeItems> = coroutineScope {
        withContext(Dispatchers.IO) {
            val list = mutableListOf<HomeItems>()
            val res = async {
                try {
                    apiCases.fetchSyllabusAPI.invoke(courseSem.lowercase()).let { syllabus ->
                        Triple(
                            onlineSyllabusUIMapper.mapFromEntityList(
                                (syllabus.semester?.subjects?.theory) ?: emptyList()
                            ), onlineSyllabusUIMapper.mapFromEntityList(
                                (syllabus.semester?.subjects?.lab) ?: emptyList()
                            ), onlineSyllabusUIMapper.mapFromEntityList(
                                (syllabus.semester?.subjects?.pe) ?: emptyList()
                            )
                        ).mapToHomeItems()
                    }
                } catch (e: Exception) {
                    listOf(HomeItems.NoData)
                }
            }
            list.addAll(res.await())
            list
        }
    }

    private suspend fun getOfflineData(
        courseSem: String,
        syllabusDao: SyllabusDao,
        offlineSyllabusUIMapper: OfflineSyllabusUIMapper
    ) = coroutineScope {
        withContext(Dispatchers.IO) {
            val theory = async {
                offlineSyllabusUIMapper.mapFromEntityList(
                    syllabusDao.getSyllabusHomeList(
                        courseSem, "Theory"
                    )
                )
            }
            val lab = async {
                offlineSyllabusUIMapper.mapFromEntityList(
                    syllabusDao.getSyllabusHomeList(
                        courseSem, "Lab"
                    )
                )
            }
            val pe = async {
                offlineSyllabusUIMapper.mapFromEntityList(
                    syllabusDao.getSyllabusHomeList(
                        courseSem, "PE"
                    )
                )
            }
            Triple(theory.await(), lab.await(), pe.await()).mapToHomeItems()
        }
    }

    private suspend fun getHoliday(): List<HomeItems> = coroutineScope {
        withContext(Dispatchers.IO) {
            val calenderQuery = filterPreferences.calendar.getDisplayName(
                Calendar.MONTH, Calendar.LONG, Locale.ENGLISH
            ) ?: "January"

            val filter: (query: String, HolidayModel) -> List<Holiday> = { q, h ->
                h.holidays.filter { holiday ->
                    holiday.month == q
                }
            }
            val task = async {
                try {
                    filterPreferences.api.fetchHolidayApi.invoke(
                        calenderQuery, filter
                    ).holidays.map {
                        HomeItems.Holiday(it)
                    }
                } catch (e: Exception) {
                    emptyList<HomeItems>()
                }
            }
            task.await()
        }
    }.let {
        if (it.isNotEmpty()) {
            listOf(HomeItems.Title("Holiday")).plus(it)
        } else emptyList()
    }

    private suspend fun getEvent(): List<HomeItems>? = coroutineScope {
        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val list = mutableListOf<HomeItems>()
                filterPreferences.firebaseCases.eventWithAttach.invoke { events ->
                    events.filter {
                        Date(
                            System.currentTimeMillis()
                        ).compareDifferenceInDays(Date(it.created!!)) <= 1
                    }.map { event ->
                        HomeViewModelExr.EventHomeModel(
                            event.title ?: "",
                            event.content ?: "",
                            event.society ?: "",
                            event.logo_link ?: "",
                            if (event.attach?.isNotEmpty() == true) event.attach!![0].link
                                ?: "" else "",
                            event.path ?: "",
                            event.created ?: 0L
                        )
                    }.let { events1 ->
                        if (events1.isNotEmpty()) {
                            list.add(HomeItems.Title("Events"))
                            list.add(HomeItems.Event(events1))
                            continuation.resumeWith(Result.success(list))
                        } else continuation.resumeWith(Result.success(emptyList()))
                    }
                }
            }
        }
    }


    //    Utils
    private fun List<LibraryModel>.filter3Day() = filter { book ->
        val diff = Date(book.returnDate).compareDifferenceInDays(
            Date(System.currentTimeMillis())
        )
        diff in 0..3 && !book.markAsReturn
    }

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

    private suspend fun getCGPA() = coroutineScope {
        withContext(Dispatchers.IO) {
            val task = async {
                val list = mutableListOf<HomeItems>()
                if (!filterPreferences.cgpa.isAllZero) {
                    list.add(HomeItems.Title("CGPA"))
                    list.add(HomeItems.Cgpa(filterPreferences.cgpa))
                }
                list
            }
            task.await()
        }
    }

    private suspend fun getAttendance() = coroutineScope {
        withContext(Dispatchers.IO) {
            val task = async {
                val list = mutableListOf<HomeItems>()
                if (filterPreferences.attendance.isNotEmpty()) {
                    list.add(HomeItems.Title("Attendance"))
                    val totalClass = filterPreferences.attendance.sumOf { it.total }
                    val totalPresent = filterPreferences.attendance.sumOf { it.present }
                    val data = HomeViewModelExr.AttendanceHomeModel(
                        totalClass, totalPresent, filterPreferences.attendance
                    )
                    list.add(HomeItems.Attendance(data))
                }
                list
            }
            task.await()
        }
    }

    companion object {
        suspend fun getEventSearch(
            firebaseCases: FirebaseCases
        ): List<EventModel> = coroutineScope {
            withContext(Dispatchers.IO) {
                suspendCoroutine { continuation ->
                    firebaseCases.eventWithAttach.invoke { events ->
                        continuation.resumeWith(Result.success(events))
                    }
                }
            }
        }
    }
}

