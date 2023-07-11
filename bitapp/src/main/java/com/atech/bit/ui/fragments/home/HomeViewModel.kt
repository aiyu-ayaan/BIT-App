package com.atech.bit.ui.fragments.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.atech.bit.ui.fragments.home.HomeViewModelExr.devNote
import com.atech.bit.ui.fragments.home.HomeViewModelExr.getHoliday
import com.atech.bit.ui.fragments.home.HomeViewModelExr.offlineDataSource
import com.atech.bit.ui.fragments.home.HomeViewModelExr.offlineDataSourceSearch
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
import com.atech.core.firebase.firestore.NoticeModel
import com.atech.core.retrofit.ApiCases
import com.atech.core.retrofit.client.Holiday
import com.atech.core.room.attendance.AttendanceDao
import com.atech.core.room.library.LibraryModel
import com.atech.core.room.syllabus.SyllabusDao
import com.atech.core.utils.DEFAULT_QUERY
import com.atech.course.sem.adapter.OfflineSyllabusUIMapper
import com.atech.course.sem.adapter.OnlineSyllabusUIMapper
import com.atech.theme.compareDifferenceInDays
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
    private var eventData: MutableList<EventModel> = mutableListOf()
    val homeScreenData: Flow<List<HomeItems>> = _homeScreenData
    var courseSem = ""
    var defPercentage = 75

    init {
        combine(
            dataStores.asFlow(), isOnline, firebaseCases.getData.invoke(
                EventModel::class.java, Db.Event
            ), syllabusDao.getSyllabusHome(
                "", ""
            ), attendanceDao.getAllAttendance(), libraryDao.getAll()
        ) { dataStores, isOnline, events, _, attendance, library ->
            courseSem = dataStores.courseWithSem
            defPercentage = dataStores.defPercentage
            eventData = events?.toMutableList() ?: mutableListOf()
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
                if (it.isNotEmpty()) {
                    homeItems.add(HomeItems.Title("Event"))
                    homeItems.add(
                        HomeItems.Event(getEvents(it).also { list ->
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

//        __________________________________ Search _______________________________________________
        viewModelScope.launch(Dispatchers.IO) {
            searchQuery.combine(filterState) { q, f ->
                q to f
            }.collectLatest { (query, filter) ->
                val list = mutableListOf<HomeItems>()
                if (query == DEFAULT_QUERY || query.isBlank()) {
                    _homeScreenSearchData.value = list
                    return@collectLatest
                }

                list.addAll(
                    filter.filter(
                        searchQuery = query,
                        allAction = ::filterAll,
                        syllabusOfflineAction = ::getOfflineSyllabus,
                        holidayAction = ::getHolidays,
                        eventAction = ::getEvents,
                        noticeAction = ::getNotice
                    )
                )
                _homeScreenSearchData.value = list
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

    //    ____________________________________ Search___________________________________________________

    val searchQuery = MutableStateFlow(DEFAULT_QUERY)
    val filterState = MutableStateFlow(FilterState().copy(all = true))
    private val _homeScreenSearchData: MutableStateFlow<List<HomeItems>> =
        MutableStateFlow(emptyList())
    val homeScreenSearchData: Flow<List<HomeItems>> = _homeScreenSearchData

    data class FilterState(
        val all: Boolean = false,
        val syllabusOnline: Boolean = false,
        val syllabusOffline: Boolean = false,
        val holiday: Boolean = false,
        val event: Boolean = false,
        val notice: Boolean = false,
    )

    private fun FilterState.filter(
        searchQuery: String,
        allAction: (String) -> List<HomeItems> = { emptyList() },
        syllabusOnlineAction: (String) -> List<HomeItems> = { emptyList() },
        syllabusOfflineAction: (String) -> List<HomeItems> = { emptyList() },
        holidayAction: (String) -> List<HomeItems> = { emptyList() },
        eventAction: (String) -> List<HomeItems> = { emptyList() },
        noticeAction: (String) -> List<HomeItems> = { emptyList() },
    ) = this.run {
        when {
            all -> allAction(searchQuery)
            syllabusOnline -> syllabusOnlineAction(searchQuery)
            syllabusOffline -> syllabusOfflineAction(searchQuery)
            holiday -> holidayAction(searchQuery)
            event -> eventAction(searchQuery)
            notice -> noticeAction(searchQuery)
            else -> emptyList()
        }
    }

    private fun filterAll(query: String) = viewModelScope.async(Dispatchers.IO) {
        val list = mutableListOf<HomeItems>()
        list.addAll(getOfflineSyllabus(query))
        getHolidays(query).let {
            if (it.isNotEmpty()) {
                list.add(HomeItems.Title("Holidays"))
                list.addAll(it)
            }
        }
        getEvents(query).let {
            if (it.isNotEmpty()) if ((it[0] as HomeItems.Event).data.isNotEmpty()) {
                list.add(HomeItems.Title("Events"))
                list.addAll(it)
            }
        }
        getNotice(query).let {
            if (it.isNotEmpty()) {
                list.add(HomeItems.Title("Notice"))
                list.addAll(it)
            }
        }
        list
    }.let {
        runBlocking {
            it.await()
        }
    }

    private fun getOfflineSyllabus(queue: String) = viewModelScope.async(Dispatchers.IO) {
        offlineDataSourceSearch(
            syllabusDao, offlineSyllabusUIMapper, queue
        )
    }.let {
        runBlocking {
            it.await()
        }
    }

    private fun getHolidays(query: String) = viewModelScope.async(Dispatchers.IO) {
        getHoliday(api, query, filter = { q, h ->
            val filterList = mutableListOf<Holiday>()
            h.holidays.filter { it.day.contains(q, true) }.toCollection(filterList)
            h.holidays.filter { it.date.lowercase().contains(q) }.let {
                it.filter { date ->
                    !filterList.any { it.date == date.date }
                }.toCollection(filterList)
            }

            h.holidays.filter { it.month.lowercase().contains(q) }.let {
                it.filter { month ->
                    !filterList.any { it.month == month.month }
                }.toCollection(filterList)
            }
            h.holidays.filter { it.occasion.lowercase().contains(q) }.let {
                it.filter { occasion ->
                    !filterList.any { it.occasion == occasion.occasion }
                }.toCollection(filterList)
            }
            filterList
        })
    }.let {
        runBlocking {
            it.await()
        }
    }

    private fun getEvents(query: String): List<HomeItems> {
        val list = mutableListOf<HomeItems>()
        val filterEvents = eventData.filter {
            it.title!!.contains(query, true)
        }.map { event ->
            HomeViewModelExr.EventHomeModel(
                event.title ?: "",
                event.content ?: "",
                event.society ?: "",
                event.logo_link ?: "",
                "",
                event.path ?: "",
                event.created ?: 0L
            )
        }
        list.add(HomeItems.Event(filterEvents))
        return list
    }

    private fun getNotice(query: String): List<HomeItems> = viewModelScope.async(Dispatchers.IO) {
        val channel = Channel<List<HomeItems>>(Channel.CONFLATED)
        viewModelScope.launch {
            firebaseCases.getData.invoke(NoticeModel::class.java, Db.Notice)
                .mapNotNull { notice ->
                    notice?.filter {
                        it.title?.contains(query, true) == true
                    }?.map { noticeModel ->
                        HomeItems.Notice(noticeModel)
                    } ?: emptyList()
                }.collect { notices ->
                    channel.send(notices)
                }

            channel.close()
        }

        val list = channel.receive()
        Log.d("AAA", "getNotice: ${list.size}")
        list
    }.let {
        runBlocking {
            it.await()
        }
    }


}

