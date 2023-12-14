package com.atech.attendance.screen.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.atech.core.data_source.room.attendance.AttendanceModel
import com.atech.core.use_case.AttendanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val case: AttendanceUseCase
) : ViewModel() {
    private val _attendance = MutableStateFlow<PagingData<AttendanceModel>>(PagingData.empty())
    val attendance: StateFlow<PagingData<AttendanceModel>> get() = _attendance.asStateFlow()
    private var attendanceGetJob: Job? = null
    private var recentlyDeletedAttendance: AttendanceModel? = null

    private val _oneTimeAttendanceScreenEvent = MutableSharedFlow<OneTimeAttendanceEvent>()

    val oneTimeAttendanceScreenEvent = _oneTimeAttendanceScreenEvent.asSharedFlow()

    init {
        getAttendance()
    }

    fun onEvent(event: AttendanceEvent) {
        when (event) {
            is AttendanceEvent.ChangeAttendanceValue -> {
                viewModelScope.launch {
                    case.updatePresentOrTotal(
                        attendance = event.attendanceModel,
                        isPresent = event.isPresent
                    )
                    getAttendance()
                }
            }

            is AttendanceEvent.UndoAttendanceState -> {
                viewModelScope.launch {
                    case.undoAttendance.invoke(event.attendanceModel)
                    getAttendance()
                }
            }

            is AttendanceEvent.ArchiveAttendance -> {
                viewModelScope.launch {
                    case.archiveAttendance(event.attendanceModel)
                    getAttendance()
                }
            }

            is AttendanceEvent.DeleteAttendance -> {
                viewModelScope.launch {
                    recentlyDeletedAttendance = event.attendanceModel
                    case.deleteAttendance(event.attendanceModel)
                    _oneTimeAttendanceScreenEvent.emit(
                        OneTimeAttendanceEvent.ShowUndoDeleteAttendanceMessage(
                            "Attendance Deleted"
                        )
                    )
                    getAttendance()
                }
            }

            AttendanceEvent.RestorerAttendance -> {
                viewModelScope.launch {
                    case.addAttendance(recentlyDeletedAttendance ?: return@launch)
                    recentlyDeletedAttendance = null
                    getAttendance()
                }
            }
        }
    }

    private fun getAttendance() {
        attendanceGetJob?.cancel()
        attendanceGetJob = case.getAllAttendance()
            .cachedIn(viewModelScope)
            .onEach {
                _attendance.value = it
            }.launchIn(viewModelScope)
    }

    sealed class OneTimeAttendanceEvent {
        data class ShowUndoDeleteAttendanceMessage(val message: String) : OneTimeAttendanceEvent()
    }
}