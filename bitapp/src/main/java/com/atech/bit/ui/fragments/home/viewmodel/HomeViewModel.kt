package com.atech.bit.ui.fragments.home.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.atech.bit.ui.fragments.home.adapter.HomeItems
import com.atech.bit.ui.fragments.home.util.GetHomeData
import com.atech.bit.ui.fragments.home.viewmodel.HomeViewModelExr.getHoliday
import com.atech.bit.ui.fragments.home.viewmodel.HomeViewModelExr.offlineDataSourceSearch
import com.atech.core.data.room.library.LibraryDao
import com.atech.core.datastore.DataStoreCases
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
import com.atech.core.utils.SYLLABUS_SOURCE_DATA
import com.atech.core.utils.SharePrefKeys
import com.atech.core.utils.fromJSON
import com.atech.course.sem.adapter.OfflineSyllabusUIMapper
import com.atech.course.sem.adapter.OnlineSyllabusUIMapper
import com.atech.course.utils.SyllabusEnableModel
import com.atech.course.utils.compareToCourseSem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: ApiCases,
    private val syllabusDao: SyllabusDao,
    private val libraryDao: LibraryDao,
    private val offlineSyllabusUIMapper: OfflineSyllabusUIMapper,
    private val onlineSyllabusUIMapper: OnlineSyllabusUIMapper,
    private val firebaseCases: FirebaseCases,
    private val state: SavedStateHandle,
    val pref: SharedPreferences,
    private val attendanceDao: AttendanceDao,
    private val calendar: Calendar,
    dataStoreCases: DataStoreCases
) : ViewModel() {

    var uninstallDialogSeen: Boolean
        get() = state["uninstallDialogSeen"] ?: false
        set(value) {
            state["uninstallDialogSeen"] = value
        }

    var isAnnouncementDialogShown: Boolean = state["isAnnouncementDialogShown"] ?: true
        set(value) {
            field = value
            state["isAnnouncementDialogShown"] = value
        }


    val isOnline = MutableStateFlow(false)
    val isPermissionGranted = MutableStateFlow(false)
    private val dataStores = dataStoreCases.getAll.invoke()
    private var eventData: MutableList<EventModel> = mutableListOf()
    var courseSem = ""
    var defPercentage = 7

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getHomeData() = viewModelScope.async {
        combine(
            dataStores.asFlow(),
            isOnline.combine(isPermissionGranted) { isOnline, isPermissionGranted ->
                isOnline to isPermissionGranted
            },
            attendanceDao.getAllAttendance().combine(
                libraryDao.getAll()
            ) { attendance, library ->
                attendance to library
            },
            syllabusDao.getSyllabusHome(
                "", ""
            ),
        ) { pref, (isOnline, permission), (attendance, library), _ ->
            DataSetForHome(
                pref.courseWithSem,
                isOnline,
                permission,
                attendance,
                library,
                syllabusDao,
                offlineSyllabusUIMapper,
                onlineSyllabusUIMapper,
                api,
                calendar,
                firebaseCases,
                pref.cgpa
            )
        }.flatMapLatest {
            GetHomeData(it).getHomeItems()
        }
    }

    fun observerEventSearch() = viewModelScope.launch {
        GetHomeData.getEventSearch(firebaseCases).let {
            eventData = it.toMutableList()
        }
    }

    fun observeData(owner: LifecycleOwner) {
        dataStores.observe(owner) {
            courseSem = it.courseWithSem
            defPercentage = it.defPercentage
            val syllabusEnableModel = pref.getString(
                SharePrefKeys.KeyToggleSyllabusSource.name, SYLLABUS_SOURCE_DATA
            )?.let { value ->
                fromJSON(value, SyllabusEnableModel::class.java)!!
            } ?: fromJSON(
                SYLLABUS_SOURCE_DATA, SyllabusEnableModel::class.java
            )!!
            this.isOnline.value = syllabusEnableModel.compareToCourseSem(it.courseWithSem)
        }
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

    suspend fun observeSearchData(): Flow<List<HomeItems>> =
        withContext(Dispatchers.IO) {
            searchQuery.combine(filterState) { q, f ->
                q to f
            }.flatMapLatest { (query, filter) ->
                val list = mutableListOf<HomeItems>()
                if (query == DEFAULT_QUERY || query.isBlank()) {
                    emptyFlow()
                } else {
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
                    flowOf(list)
                }
            }
        }


    data class FilterState(
        val all: Boolean = false,
        val syllabusOnline: Boolean = false,
        val syllabusOffline: Boolean = false,
        val holiday: Boolean = false,
        val event: Boolean = false,
        val notice: Boolean = false,
    )

    private suspend fun FilterState.filter(
        searchQuery: String,
        allAction: suspend (String) -> List<HomeItems> = { emptyList() },
        syllabusOnlineAction: suspend (String) -> List<HomeItems> = { emptyList() },
        syllabusOfflineAction: suspend (String) -> List<HomeItems> = { emptyList() },
        holidayAction: suspend (String) -> List<HomeItems> = { emptyList() },
        eventAction: suspend (String) -> List<HomeItems> = { emptyList() },
        noticeAction: suspend (String) -> List<HomeItems> = { emptyList() },
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

    private suspend fun getNotice(query: String): List<HomeItems> =
        suspendCoroutine { continuation ->
            viewModelScope.launch(Dispatchers.IO) {
                val channel = Channel<List<HomeItems>>(Channel.CONFLATED)
                val job = launch {
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

                continuation.resumeWith(Result.success(runBlocking {
                    val list = channel.receive()
                    job.cancel()
                    list
                }))
            }
        }

}

