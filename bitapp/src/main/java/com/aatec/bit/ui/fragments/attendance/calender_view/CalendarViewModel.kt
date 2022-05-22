/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 2/13/22, 10:50 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 2/13/22, 8:05 PM
 */



package com.aatec.bit.ui.fragments.attendance.calender_view

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.aatec.core.data.room.attendance.AttendanceModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val state: SavedStateHandle
) : ViewModel() {
    val attendance = state.get<AttendanceModel>("attendance")
    val title = state.get<String>("title")
    val minPercentage = state.get<Int>("minPercentage")
}