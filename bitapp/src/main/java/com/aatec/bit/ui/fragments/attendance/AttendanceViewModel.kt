package com.aatec.bit.ui.fragments.attendance

import androidx.lifecycle.*
import com.aatec.bit.utils.AttendanceEvent
import com.aatec.core.data.room.attendance.AttendanceDao
import com.aatec.core.data.room.attendance.AttendanceModel
import com.aatec.core.data.room.syllabus.SyllabusDao
import com.aatec.core.utils.REQUEST_ADD_SUBJECT_FROM_SYLLABUS
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

    private val _attendance = attendanceDao.getAllAttendance()
    val attendance: LiveData<List<AttendanceModel>>
        get() = _attendance.asLiveData()


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