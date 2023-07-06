package com.atech.bit.ui.fragments.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.atech.bit.ui.fragments.home.HomeViewModelExr.devNote
import com.atech.bit.ui.fragments.home.HomeViewModelExr.offlineDataSource
import com.atech.bit.ui.fragments.home.HomeViewModelExr.onlineDataSource
import com.atech.bit.ui.fragments.home.HomeViewModelExr.topView
import com.atech.bit.ui.fragments.home.adapter.HomeItems
import com.atech.core.data.room.library.LibraryDao
import com.atech.core.datastore.DataStoreCases
import com.atech.core.retrofit.ApiCases
import com.atech.core.room.attendance.AttendanceDao
import com.atech.core.room.syllabus.SyllabusDao
import com.atech.course.sem.adapter.OfflineSyllabusUIMapper
import com.atech.course.sem.adapter.OnlineSyllabusUIMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: ApiCases,
    private val pref: DataStoreCases,
    private val syllabusDao: SyllabusDao,
    private val attendanceDao: AttendanceDao,
    private val libraryDao: LibraryDao,
    private val offlineSyllabusUIMapper: OfflineSyllabusUIMapper,
    private val onlineSyllabusUIMapper: OnlineSyllabusUIMapper
) : ViewModel() {

    val isOnline = MutableStateFlow(false)
    val allData = pref.getAll.invoke()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun homeScreenData() = allData.asFlow().combine(isOnline) { pref, isOnline ->
        pref to isOnline
    }.flatMapLatest { (pref, isOnline) ->
        Log.d("AAA", "homeScreenData: $isOnline")
        val homeItems = mutableListOf<HomeItems>()
        topView(homeItems)
        homeItems.addAll(
            if (isOnline) {
                onlineDataSource(
                    api, onlineSyllabusUIMapper, pref.courseWithSem
                )
            } else offlineDataSource(
                syllabusDao, offlineSyllabusUIMapper, pref.courseWithSem
            )
        )
        homeItems.add(devNote)
        flowOf(homeItems)
    }.asLiveData()


}

