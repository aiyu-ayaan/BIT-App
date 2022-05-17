/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/1/21, 1:09 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/1/21, 11:14 AM
 */



package com.aatec.bit.fragments.attendance.reset_attendance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.aatec.core.data.room.attendance.AttendanceDao
import com.aatec.core.data.room.attendance.AttendanceModel
import com.aatec.core.utils.BitAppScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val attendanceDao: AttendanceDao,
    @BitAppScope private val scope: CoroutineScope
) : ViewModel() {
    val attendance = state.get<AttendanceModel>("attendance")

    fun update(attendanceModel: AttendanceModel) = scope.launch {
        attendanceDao.update(attendanceModel)
    }
}