package com.atech.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.atech.core.room.attendance.AttendanceDao
import com.atech.core.room.attendance.AttendanceModel
import com.atech.core.room.syllabus.SyllabusDao
import com.atech.core.room.syllabus.SyllabusModel
import com.atech.core.utils.REQUEST_ADD_SUBJECT_FROM_SYLLABUS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceDao: AttendanceDao,
    private val syllabusDao: SyllabusDao
) : ViewModel() {


    private val _attendance = attendanceDao.getNonArchiveAttendance()
    val unArchive: LiveData<List<AttendanceModel>>
        get() = _attendance.asLiveData()

    val allAttendance = attendanceDao.getAllAttendance().asLiveData()


    val _attendanceEvent = Channel<AttendanceEvent>()
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

    /**
     *Event when attendance is deleted
     *@author Ayaan
     *@since 4.0.3
     */
    sealed class AttendanceEvent {
        /**
         * Note :- syllabus is not use in FragmentEditSyllabus
         * @param attendance AttendanceModel
         * @param syllabus SyllabusModel
         * @since 4.0.3
         */
        data class ShowUndoDeleteMessage(
            val attendance: AttendanceModel,
            val syllabus: SyllabusModel? = null,
        ) : AttendanceEvent()

    }


}