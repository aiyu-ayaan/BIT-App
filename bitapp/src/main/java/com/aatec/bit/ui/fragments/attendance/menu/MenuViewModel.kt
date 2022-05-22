/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 2/22/22, 5:37 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 2/22/22, 5:37 PM
 */

package com.aatec.bit.ui.fragments.attendance.menu

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aatec.core.data.room.attendance.AttendanceDao
import com.aatec.core.data.room.attendance.AttendanceModel
import com.aatec.core.data.room.syllabus.SyllabusDao
import com.aatec.core.utils.REQUEST_ADD_SUBJECT_FROM_SYLLABUS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val attendanceDao: AttendanceDao,
    private val syllabusDao: SyllabusDao
) : ViewModel() {


    val attendance: AttendanceModel? = state.get<AttendanceModel>("attendance")


    fun delete(attendanceModel: AttendanceModel) =
        viewModelScope.launch {
            syllabusDao.updateSyllabusAddedInAttendance(attendanceModel.subject, 0)
            attendanceDao.delete(attendanceModel)

        }

    //    IO function
    fun update(attendanceModel: AttendanceModel) = viewModelScope.launch {
        attendanceDao.update(attendanceModel)
    }

    fun add(attendanceModel: AttendanceModel, request: Int) = viewModelScope.launch {
        if (request == REQUEST_ADD_SUBJECT_FROM_SYLLABUS) syllabusDao.updateSyllabusAddedInAttendance(
            attendanceModel.subject,
            1
        )
        attendanceDao.insert(attendanceModel)
    }
}