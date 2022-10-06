package com.atech.bit.ui.fragments.attendance.archive

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.core.data.room.attendance.AttendanceDao
import com.atech.core.data.room.attendance.AttendanceModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val dao: AttendanceDao
) : ViewModel() {

    val archiveAttendance = dao.getAllArchiveAttendance()

    val defPercentage = state.get<Int>("defPercentage") ?: 75


    fun update(attendanceModel: AttendanceModel) = viewModelScope.launch {
        dao.update(attendanceModel)
    }

    fun delete(attendanceModel: AttendanceModel) = viewModelScope.launch {
        dao.delete(attendanceModel)
    }

    fun deleteAll() = viewModelScope.launch {
        dao.deleteAllArchiveAttendance()
    }
}