package com.atech.bit.ui.fragments.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.atech.bit.ui.fragments.home.HomeViewModelExr.devNote
import com.atech.bit.ui.fragments.home.HomeViewModelExr.getHoliday
import com.atech.bit.ui.fragments.home.HomeViewModelExr.offlineDataSource
import com.atech.bit.ui.fragments.home.HomeViewModelExr.onlineDataSource
import com.atech.bit.ui.fragments.home.HomeViewModelExr.topView
import com.atech.bit.ui.fragments.home.adapter.HomeItems
import com.atech.bit.utils.combine
import com.atech.core.data.room.library.LibraryDao
import com.atech.core.datastore.DataStoreCases
import com.atech.core.datastore.FilterPreferences
import com.atech.core.firebase.firestore.Db
import com.atech.core.firebase.firestore.EventModel
import com.atech.core.firebase.firestore.FirebaseCases
import com.atech.core.retrofit.ApiCases
import com.atech.core.room.attendance.AttendanceDao
import com.atech.core.room.library.LibraryModel
import com.atech.core.room.syllabus.SyllabusDao
import com.atech.course.sem.adapter.OfflineSyllabusUIMapper
import com.atech.course.sem.adapter.OnlineSyllabusUIMapper
import com.atech.theme.compareDifferenceInDays
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: ApiCases,
    private val pref: DataStoreCases,
    private val syllabusDao: SyllabusDao,
    private val attendanceDao: AttendanceDao,
    private val libraryDao: LibraryDao,
    private val offlineSyllabusUIMapper: OfflineSyllabusUIMapper,
    private val onlineSyllabusUIMapper: OnlineSyllabusUIMapper,
    private val firebaseCases: FirebaseCases,
    calendar: Calendar,
) : ViewModel() {

    val isOnline = MutableStateFlow(false)
    private val dataStores = pref.getAll.invoke()
    private val calenderQuery =
        calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) ?: "January"
    private val _homeScreenData: MutableStateFlow<List<HomeItems>> = MutableStateFlow(emptyList())
    val homeScreenData: Flow<List<HomeItems>> = _homeScreenData
    var courseSem = ""
    var defPercentage = 75

    init {
        combine(
            dataStores.asFlow(),
            isOnline,
            firebaseCases.getData.invoke(
                EventModel::class.java, Db.Event
            ),
            syllabusDao.getSyllabusHome(
                "", ""
            ),
            attendanceDao.getAllAttendance(),
            libraryDao.getAll()
        ) { dataStores, isOnline, events, _, attendance, library ->
            courseSem = dataStores.courseWithSem
            defPercentage = dataStores.defPercentage
            val homeItems = mutableListOf<HomeItems>()
            topView(homeItems, library)

            homeItems.addAll(
                getSyllabusData(isOnline, dataStores).await()
            )
            val holidays = getHoliday(
                api,
                calenderQuery,
            )
            if (holidays.isNotEmpty()) {
                homeItems.add(HomeItems.Title("Holiday"))
                homeItems.addAll(
                    holidays
                )
            }
            events?.filter {
                Date().compareDifferenceInDays(Date(it.created ?: 0)) <= 7
            }?.let {
                Log.d("AAA", "${it.size} getEvents")
                if (it.isNotEmpty()) {
                    homeItems.add(HomeItems.Title("Event"))
                    homeItems.add(
                        HomeItems.Event(
                            getEvents(it).also { list ->
                                list.reverse()
                            })
                    )
                }
            }

            if (!dataStores.cgpa.isAllZero) {
                homeItems.add(HomeItems.Title("CGPA"))
                homeItems.add(HomeItems.Cgpa(dataStores.cgpa))
            }
            if (attendance.isNotEmpty()) {
                homeItems.add(HomeItems.Title("Attendance"))
                val totalClass = attendance.sumOf { it.total }
                val totalPresent = attendance.sumOf { it.present }
                val data = HomeViewModelExr.AttendanceHomeModel(
                    totalClass, totalPresent, attendance
                )
                homeItems.add(HomeItems.Attendance(data))
            }
//            End
            homeItems.add(devNote)
            homeItems
        }.also {
            viewModelScope.launch(Dispatchers.IO) {
                it.collectLatest {
                    _homeScreenData.value = it
                }
            }
        }
    }

    private suspend fun getEvents(
        events: List<EventModel>?
    ) = withContext(Dispatchers.IO) {
        val list = mutableListOf<HomeViewModelExr.EventHomeModel>()
        (events?.map { event ->
            HomeViewModelExr.EventHomeModel(
                event.title ?: "",
                event.content ?: "",
                event.society ?: "",
                event.logo_link ?: "",
                "",
                event.path ?: "",
                event.created ?: 0L
            )
        } ?: emptyList()).filter {
            Log.d(
                "AAA",
                "getEvents: ${
                    Date(
                        System.currentTimeMillis()
                    ).compareDifferenceInDays(Date(it.created)) <= 1
                } "
            )
            Date(
                System.currentTimeMillis()
            ).compareDifferenceInDays(Date(it.created)) <= 1
        }.map {
            firebaseCases.getAttach.invoke(Db.Event, it.path).map { attaches ->
                it.copy(
                    posterLink = if (attaches.size == 0) "" else attaches[0].link ?: "",
                )
            }
        }.distinct().forEach {
            viewModelScope.launch(Dispatchers.IO) {
                it.collectLatest { event ->
                    list.add(event)
                }
            }
        }
        list
    }

    private fun getSyllabusData(
        isOnline: Boolean, pref: FilterPreferences
    ) = viewModelScope.async(Dispatchers.IO) {
        if (isOnline) {
            onlineDataSource(
                api, onlineSyllabusUIMapper, pref.courseWithSem
            )
        } else offlineDataSource(
            syllabusDao, offlineSyllabusUIMapper, pref.courseWithSem
        )
    }

    fun deleteBook(libraryModel: LibraryModel) = viewModelScope.launch {
        libraryDao.deleteBook(libraryModel)
    }

    fun updateBook(libraryModel: LibraryModel) = viewModelScope.launch {
        libraryDao.updateBook(libraryModel)
    }


}

