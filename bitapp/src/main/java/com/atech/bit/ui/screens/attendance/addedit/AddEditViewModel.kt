package com.atech.bit.ui.screens.attendance.addedit

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.core.datasource.room.attendance.AttendanceModel
import com.atech.core.usecase.AttendanceUseCase
import com.atech.core.utils.toBoolean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val useCase: AttendanceUseCase, state: SavedStateHandle
) : ViewModel() {
    private val _id = state.get<Int>("attendanceId") ?: -1

    val fromAddFromSyllabus = (state.get<Int>("fromAddFromSyllabus") ?: 0).toBoolean()

    val isEdit = _id != -1

    private val _attendanceModel = mutableStateOf<AttendanceModel?>(null)

    private val _subject = mutableStateOf(_attendanceModel.value?.subject ?: "")
    val subject: State<String> get() = _subject

    private val _present = mutableIntStateOf(_attendanceModel.value?.present ?: 0)
    val present: State<Int> get() = _present

    private val _total = mutableIntStateOf(_attendanceModel.value?.total ?: 0)
    val total: State<Int> get() = _total

    private val _teacherName = mutableStateOf(_attendanceModel.value?.teacher ?: "")
    val teacherName: State<String> get() = _teacherName

    private val _oneTimeEvent = MutableSharedFlow<AddEditOneTimeEvent>()
    val oneTimeEvent = _oneTimeEvent.asSharedFlow()

    init {
        if (_id != -1) {
            getElement(_id)
        }
    }

    fun onEvent(event: AddEditEvent) {
        when (event) {
            is AddEditEvent.OnSubjectChange -> _subject.value = event.subject
            is AddEditEvent.OnTeacherNameChange -> _teacherName.value = event.teacherName
            is AddEditEvent.OnPresentChange -> _present.intValue = event.present
            is AddEditEvent.OnTotalChange -> _total.intValue = event.total
            is AddEditEvent.OnSaveClick -> save(event.action)
        }
    }

    private fun save(action: () -> Unit) = viewModelScope.launch {
        if (_id == -1)
            useCase.addAttendance.invoke(
                AttendanceModel(
                    subject = _subject.value,
                    present = _present.intValue,
                    total = _total.intValue,
                    teacher = _teacherName.value
                )
            )
        else {
            try {
                useCase.updateAttendance.invoke(
                    _attendanceModel.value!!,
                    AttendanceModel(
                        id = _id,
                        subject = _subject.value,
                        present = _present.intValue,
                        total = _total.intValue,
                        teacher = _teacherName.value
                    )
                )
            } catch (e: Exception) {
                _oneTimeEvent.emit(
                    AddEditOneTimeEvent.ShowSnackBar(
                        "Subject name already exist"
                    )
                )
            }
        }

        action.invoke()
    }


    private fun getElement(id: Int) = viewModelScope.launch {
        useCase.getAttendanceById.invoke(id)?.let { model ->
            _attendanceModel.value = model
            _subject.value = model.subject
            _present.intValue = model.present
            _total.intValue = model.total
            _teacherName.value = model.teacher ?: ""
        }
    }

    sealed class AddEditOneTimeEvent {
        data class ShowSnackBar(val message: String) : AddEditOneTimeEvent()
    }
}