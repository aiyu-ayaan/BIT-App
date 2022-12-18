/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/23/22, 12:36 AM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/22/22, 11:26 PM
 */



package com.atech.bit.ui.fragments.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.atech.bit.utils.SyllabusEnableModel
import com.atech.core.api.ApiRepository
import com.atech.core.data.room.attendance.AttendanceDao
import com.atech.core.data.room.library.LibraryDao
import com.atech.core.data.room.library.LibraryModel
import com.atech.core.data.room.syllabus.SyllabusDao
import com.atech.core.data.ui.events.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val apiRepository: ApiRepository,
    private val calendar: Calendar,
    private val syllabusDao: SyllabusDao,
    private val attendanceDao: AttendanceDao,
    private val eventRepository: EventRepository,
    private val libraryDao: LibraryDao
) : ViewModel() {

    private val calenderQuery =
        calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) ?: "January"


    fun getHoliday() = apiRepository.getHolidayData(calenderQuery, filter = { query, allHoliday ->
        allHoliday.holidays.filter { it.month == query }
    }).asLiveData()


    var syllabusEnableModel: SyllabusEnableModel =
        SyllabusEnableModel()

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


    fun getEvent(start: Long, end: Long) =
        eventRepository.getEvent7Days(start, end).asLiveData()

    fun getOnlineSyllabus() = syllabusQuery.flatMapLatest { semester ->
        apiRepository.getSyllabus(semester.lowercase())
    }.asLiveData()

    fun getLibrary() = libraryDao.getAll().asLiveData()

    fun updateBook(libraryModel: LibraryModel) = viewModelScope.launch {
        libraryDao.updateBook(libraryModel)
    }
    fun deleteBook(libraryModel: LibraryModel) = viewModelScope.launch {
        libraryDao.deleteBook(libraryModel)
    }
}