package com.atech.attendance.screen.attendance

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.atech.core.data_source.room.attendance.AttendanceModel
import com.atech.core.use_case.AttendanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val case: AttendanceUseCase
) : ViewModel() {
    private val _attendance = MutableStateFlow<PagingData<AttendanceModel>>(PagingData.empty())
    val attendance: StateFlow<PagingData<AttendanceModel>> get() = _attendance.asStateFlow()
    private var attendanceGetJob: Job? = null

    init {
        getAttendance()
    }

    private fun getAttendance() {
        attendanceGetJob?.cancel()
        attendanceGetJob = case.getAllAttendance()
            .cachedIn(viewModelScope)
            .onEach {
                _attendance.value = it
            }.launchIn(viewModelScope)
    }


}