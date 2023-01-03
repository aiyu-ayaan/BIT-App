/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 2/13/22, 10:50 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 2/13/22, 8:36 PM
 */



package com.atech.bit.ui.fragments.edit_subject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.atech.core.utils.AttendanceEvent
import com.atech.core.data.room.attendance.AttendanceDao
import com.atech.core.data.room.attendance.AttendanceModel
import com.atech.core.data.room.syllabus.SyllabusDao
import com.atech.core.data.room.syllabus.SyllabusModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EditSyllabusViewModel @Inject constructor(
    private val syllabusDao: SyllabusDao,
    private val attendanceDao: AttendanceDao
) : ViewModel() {

    val syllabusQuery = MutableStateFlow("-fdfjdfk")
    private val editSyllabusEvent = Channel<AttendanceEvent>()

    val editSyllabusEventFlow = editSyllabusEvent.receiveAsFlow()

    val syllabus = syllabusQuery.flatMapLatest {
        syllabusDao.getSyllabusEdit(it)
    }.asLiveData()

    fun updateSyllabus(syllabusModel: SyllabusModel) = viewModelScope.launch {
        syllabusDao.updateSyllabus(syllabusModel)
    }

    fun add(attendanceModel: AttendanceModel) = viewModelScope.launch {
        attendanceDao.insert(attendanceModel)
    }

    suspend fun getAttendance(query: String) =
        withContext(viewModelScope.coroutineContext) {
            attendanceDao.getAttendance(query)
        }

    fun delete(attendanceModel: AttendanceModel, syllabus: SyllabusModel) =
        viewModelScope.launch {
            attendanceDao.delete(attendanceModel)
            editSyllabusEvent.send(AttendanceEvent.ShowUndoDeleteMessage(attendanceModel, syllabus))
        }

    suspend fun findSyllabus(query: String): AttendanceModel? =
        withContext(Dispatchers.IO) {
            attendanceDao.findSyllabus(query)
        }

}