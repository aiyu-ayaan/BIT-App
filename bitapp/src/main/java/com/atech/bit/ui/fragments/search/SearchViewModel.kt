/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 2/21/22, 10:27 AM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 2/20/22, 9:06 PM
 */



package com.atech.bit.ui.fragments.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.atech.core.data.room.syllabus.SyllabusDao
import com.atech.core.data.ui.events.EventRepository
import com.atech.core.data.ui.holiday.HolidayRepository
import com.atech.core.data.ui.notice.NoticeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val syllabusDao: SyllabusDao,
    private val noticeRepository: NoticeRepository,
    private val holidayRepository: HolidayRepository,
    private val eventRepository: EventRepository
) : ViewModel() {



    val query = MutableStateFlow("")


    val subject = query.flatMapLatest {
        syllabusDao.getSyllabusSearch(it)
    }.asLiveData()

    val notice = query.flatMapLatest {
        noticeRepository.getNoticeSearch(it)
    }.asLiveData()

    val holiday = query.flatMapLatest {
        holidayRepository.getSearchHoliday(it)
    }.asLiveData()

    val event = query.flatMapLatest {
        eventRepository.getSearchEvent(it)
    }.asLiveData()
}