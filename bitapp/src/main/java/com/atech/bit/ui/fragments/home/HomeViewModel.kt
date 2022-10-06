/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/23/22, 12:36 AM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/22/22, 11:26 PM
 */



package com.atech.bit.ui.fragments.home

import androidx.lifecycle.*
import com.atech.bit.utils.MainStateEvent
import com.atech.core.data.room.attendance.AttendanceDao
import com.atech.core.data.room.syllabus.SyllabusDao
import com.atech.core.data.ui.events.EventRepository
import com.atech.core.data.ui.holiday.Holiday
import com.atech.core.data.ui.holiday.HolidayRepository
import com.atech.core.data.ui.syllabus.SyllabusRepository
import com.atech.core.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val holidayRepository: HolidayRepository,
    private val calendar: Calendar,
    private val syllabusDao: SyllabusDao,
    private val attendanceDao: AttendanceDao,
    private val syllabusRepository: SyllabusRepository,
    private val eventRepository: EventRepository,
) : ViewModel() {


    private val _dataStateMain: MutableLiveData<DataState<List<Holiday>>> = MutableLiveData()
    val dataStateMain: LiveData<DataState<List<Holiday>>>
        get() = _dataStateMain

    private val calenderQuery =
        calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) ?: "January"


    fun setStateListener(mainStateEvent: MainStateEvent) {
        viewModelScope.launch {
            when (mainStateEvent) {
                MainStateEvent.GetData -> {
                    holidayRepository.getHolidayByMonth(calenderQuery)
                        .onEach { dataState ->
                            _dataStateMain.value = dataState
                        }.launchIn(viewModelScope)
                }
                MainStateEvent.NoInternet -> {
                }
            }
        }
    }

    //    Syllabus
    val syllabusQuery = MutableStateFlow("-fdfjdfk")


    val theory = syllabusQuery.flatMapLatest {
        syllabusDao.getSyllabusHome(it, "Theory")
    }.asLiveData()


    val lab = syllabusQuery.flatMapLatest {
        syllabusDao.getSyllabusHome(it, "Lab")
    }.asLiveData()


    val pe = syllabusQuery.flatMapLatest {
        syllabusDao.getSyllabusHome(it, "PE")
    }.asLiveData()

    val attAttendance = attendanceDao.getNonArchiveAttendance().asLiveData()

    fun getSyllabus() = viewModelScope.launch {
        syllabusRepository.getSyllabus()
    }


    fun getEvent(start: Long, end: Long) =
        eventRepository.getEvent7Days(start, end).asLiveData()


}