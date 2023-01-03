package com.atech.bit.ui.fragments.attendance.add_from_online_syllabus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.atech.core.api.ApiRepository
import com.atech.core.data.room.attendance.AttendanceDao
import com.atech.core.data.room.attendance.AttendanceModel
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

@HiltViewModel
class AddFromOnlineSyllabusViewModel @Inject constructor(
    private val dao: AttendanceDao,
    private val apiRepository: ApiRepository
) : ViewModel() {

    private val editFromOnlineSyllabusEvent = Channel<AttendanceModel>()

    val editFromOnlineSyllabusFlow = editFromOnlineSyllabusEvent.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getAttendance() =
        withContext(viewModelScope.coroutineContext) {
            dao.getAllAttendance().flatMapLatest { attendanceList ->
                MutableStateFlow(attendanceList.filter { it.fromOnlineSyllabus == true })
            }
        }.asLiveData()

    val courseWithSem = MutableStateFlow("bca1")

    @OptIn(ExperimentalCoroutinesApi::class)
    val onlineSyllabus = courseWithSem.flatMapLatest { courseWithSem ->
        apiRepository.getSyllabus(courseWithSem)
    }

    fun addAttendance(attendanceModel: AttendanceModel) = viewModelScope.launch {
        dao.insert(attendanceModel)
    }

    fun deleteAttendance(attendanceModel: AttendanceModel) = viewModelScope.launch {
        dao.delete(attendanceModel)
        editFromOnlineSyllabusEvent.send(attendanceModel)
    }

    suspend fun findSyllabus(query: String): AttendanceModel? =
        withContext(Dispatchers.IO) {
            dao.findSyllabus(query)
        }
}