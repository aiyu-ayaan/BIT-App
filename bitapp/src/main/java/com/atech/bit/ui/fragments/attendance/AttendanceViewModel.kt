package com.atech.bit.ui.fragments.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val attendanceDao: AttendanceDao,
    private val syllabusDao: SyllabusDao
) : ViewModel() {




    private val _attendance = attendanceDao.getNonArchiveAttendance()
    val unArchive: LiveData<List<AttendanceModel>>
        get() = _attendance.asLiveData()

    val allAttendance = attendanceDao.getAllAttendance().asLiveData()


    private val _attendanceEvent = Channel<AttendanceEvent>()
    val attendanceEvent = _attendanceEvent.receiveAsFlow()

    //    IO function
    fun update(attendanceModel: AttendanceModel) = viewModelScope.launch {
        attendanceDao.update(attendanceModel)
    }

    fun delete(attendanceModel: AttendanceModel) =
        viewModelScope.launch {
            syllabusDao.updateSyllabusAddedInAttendance(attendanceModel.subject, 0)
            attendanceDao.delete(attendanceModel)
            _attendanceEvent.send(AttendanceEvent.ShowUndoDeleteMessage(attendanceModel))
        }

    fun add(attendanceModel: AttendanceModel, request: Int) = viewModelScope.launch {
        if (request == REQUEST_ADD_SUBJECT_FROM_SYLLABUS) syllabusDao.updateSyllabusAddedInAttendance(
            attendanceModel.subject,
            1
        )
        attendanceDao.insert(attendanceModel)
    }


}