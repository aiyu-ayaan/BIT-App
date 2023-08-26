package com.atech.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.atech.core.datastore.DataStoreCases
import com.atech.core.room.attendance.AttendanceDao
import com.atech.core.room.attendance.AttendanceModel
import com.atech.core.room.syllabus.SyllabusDao
import com.atech.core.room.syllabus.SyllabusModel
import com.atech.core.utils.REQUEST_ADD_SUBJECT_FROM_SYLLABUS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceDao: AttendanceDao,
    private val syllabusDao: SyllabusDao,
    private val cases: DataStoreCases,
) : ViewModel() {


    val getAllPref = cases.getAll.invoke()

    val sortPref = cases.attendancePref.invoke()

    val archive: LiveData<List<AttendanceModel>> =
        attendanceDao.getAllArchiveAttendance().asLiveData()

    fun getAttendance() = viewModelScope.async(Dispatchers.IO) {
        val list = cases.attendancePref.invoke().switchMap {
            attendanceDao.getAttendanceSorted(it).asLiveData()
        }
        list
    }


    val attendanceEventChannel = Channel<AttendanceEvent>()
    val attendanceEvent = attendanceEventChannel.receiveAsFlow()

    //    IO function
    fun update(attendanceModel: AttendanceModel) = viewModelScope.launch {
        attendanceDao.update(attendanceModel)
    }

    fun delete(attendanceModel: AttendanceModel) =
        viewModelScope.launch {
            syllabusDao.updateSyllabusAddedInAttendance(attendanceModel.subject, 0)
            attendanceDao.delete(attendanceModel)
            attendanceEventChannel.send(AttendanceEvent.ShowUndoDeleteMessage(attendanceModel))
        }

    fun add(attendanceModel: AttendanceModel, request: Int) = viewModelScope.launch {
        if (request == REQUEST_ADD_SUBJECT_FROM_SYLLABUS) syllabusDao.updateSyllabusAddedInAttendance(
            attendanceModel.subject,
            1
        )
        attendanceDao.insert(attendanceModel)
    }

    fun deleteAllArchive() = viewModelScope.launch {
        attendanceDao.deleteAllArchiveAttendance()
    }

    fun deleteAll() = viewModelScope.launch {
        syllabusDao.updateSyllabusAddedInAttendance()
        attendanceDao.deleteAll()
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