/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/1/21, 1:09 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/1/21, 11:14 AM
 */



package com.atech.bit.ui.fragments.attendance.list_all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.atech.core.utils.AttendanceEvent
import com.atech.core.data.room.attendance.AttendanceDao
import com.atech.core.data.room.attendance.AttendanceModel
import com.atech.core.data.room.syllabus.SyllabusDao
import com.atech.core.utils.REQUEST_ADD_SUBJECT_FROM_SYLLABUS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListAllViewModel @Inject constructor(
    private val attendanceDao: AttendanceDao,
    private val syllabusDao: SyllabusDao
) : ViewModel() {
    private val _attendanceEventFlow = Channel<AttendanceEvent>()
    private val _allAttendanceEvent: Flow<List<AttendanceModel>> =
        attendanceDao.getNonArchiveAttendance()
    val allAttendance = _allAttendanceEvent.asLiveData()

    val attendanceEvent = _attendanceEventFlow.receiveAsFlow()

    fun add(attendanceModel: AttendanceModel, request: Int) = viewModelScope.launch {
        attendanceDao.insert(attendanceModel)
        if (request == REQUEST_ADD_SUBJECT_FROM_SYLLABUS) syllabusDao.updateSyllabusAddedInAttendance(
            attendanceModel.subject,
            1
        )
    }

    fun update(attendanceModel: AttendanceModel) = viewModelScope.launch {
        attendanceDao.update(attendanceModel)
    }

    fun delete(attendanceModel: AttendanceModel) = viewModelScope.launch {
        syllabusDao.updateSyllabusAddedInAttendance(attendanceModel.subject, 0)
        attendanceDao.delete(attendanceModel)
        _attendanceEventFlow.send(AttendanceEvent.ShowUndoDeleteMessage(attendanceModel))
    }


}
