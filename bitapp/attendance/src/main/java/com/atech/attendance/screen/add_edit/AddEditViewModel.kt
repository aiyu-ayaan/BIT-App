package com.atech.attendance.screen.add_edit

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.core.data_source.room.attendance.AttendanceModel
import com.atech.core.use_case.AttendanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val useCase: AttendanceUseCase, state: SavedStateHandle
) : ViewModel() {
    private val _id = state.get<Int>("attendance_id") ?: -1

    private val _attendanceModel = mutableStateOf<AttendanceModel?>(null)

    private val _subject = mutableStateOf(_attendanceModel.value?.subject ?: "")
    val subject: State<String> get() = _subject

    private val _present = mutableIntStateOf(_attendanceModel.value?.present ?: 0)
    val present: State<Int> get() = _present

    private val _total = mutableIntStateOf(_attendanceModel.value?.total ?: 0)
    val total: State<Int> get() = _total

    private val _teacherName = mutableStateOf<String>(_attendanceModel.value?.teacher ?: "")
    val teacherName: State<String> get() = _teacherName

    init {
        if (_id != -1) {
            getElement(_id)
        }
    }

    fun onEvent(event : AddEditEvent){
        when(event){
            is AddEditEvent.OnSubjectChange -> _subject.value = event.subject
            is AddEditEvent.OnTeacherNameChange -> _teacherName.value = event.teacherName
            is AddEditEvent.OnPresentChange -> _present.intValue = event.present
            is AddEditEvent.OnTotalChange -> _total.intValue = event.total
            AddEditEvent.OnSaveClick -> save()
        }
    }

    private fun save() {

    }


    private fun getElement(id: Int) = viewModelScope.launch {
        useCase.getAttendanceById.invoke(id).let {
            _attendanceModel.value = it
        }
    }
}