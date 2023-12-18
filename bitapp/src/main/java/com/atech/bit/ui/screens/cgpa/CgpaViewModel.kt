package com.atech.bit.ui.screens.cgpa

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.core.datasource.datastore.Cgpa
import com.atech.core.usecase.DataStoreCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CgpaViewModel @Inject constructor(
    private val case: DataStoreCases
) : ViewModel() {

    private val _savedCgpa = mutableStateOf(Cgpa())
    val savedCgpa: State<Cgpa> get() = _savedCgpa

    private val _savedSem = mutableIntStateOf(1)
    val savedSem: State<Int> get() = _savedSem

    private val _savedCourse = mutableStateOf("")
    val savedCourse: State<String> get() = _savedCourse

    private val _sem1 = mutableStateOf(CgpaEditModel())
    val sem1: State<CgpaEditModel> get() = _sem1

    private val _sem2 = mutableStateOf(CgpaEditModel())
    val sem2: State<CgpaEditModel> get() = _sem2

    private val _sem3 = mutableStateOf(CgpaEditModel())
    val sem3: State<CgpaEditModel> get() = _sem3

    private val _sem4 = mutableStateOf(CgpaEditModel())
    val sem4: State<CgpaEditModel> get() = _sem4

    private val _sem5 = mutableStateOf(CgpaEditModel())
    val sem5: State<CgpaEditModel> get() = _sem5

    private val _sem6 = mutableStateOf(CgpaEditModel())
    val sem6: State<CgpaEditModel> get() = _sem6

    init {
        getAllDate()
    }


    fun onEvent(event: CGPAEvent) {
        when (event) {
            is CGPAEvent.OnSem1Change ->
                _sem1.value = _sem1.value
                    .copy(
                        semester = event.value.semester,
                        gpa = event.value.gpa
                    )

            is CGPAEvent.OnSem2Change ->
                _sem2.value = _sem2.value
                    .copy(
                        semester = event.value.semester,
                        gpa = event.value.gpa
                    )

            is CGPAEvent.OnSem3Change ->
                _sem3.value = _sem3.value
                    .copy(
                        semester = event.value.semester,
                        gpa = event.value.gpa
                    )

            is CGPAEvent.OnSem4Change ->
                _sem4.value = _sem4.value
                    .copy(
                        semester = event.value.semester,
                        gpa = event.value.gpa
                    )

            is CGPAEvent.OnSem5Change ->
                _sem5.value = _sem5.value
                    .copy(
                        semester = event.value.semester,
                        gpa = event.value.gpa
                    )

            is CGPAEvent.OnSem6Change ->
                _sem6.value = _sem6.value
                    .copy(
                        semester = event.value.semester,
                        gpa = event.value.gpa
                    )
        }
    }

    private fun getAllDate() = viewModelScope.launch {
        case.getAll.invoke().collectLatest {
            _savedCgpa.value = it.cgpa
            _savedSem.value = it.sem.toInt()
            _savedCourse.value = it.course
            _sem1.value = CgpaEditModel(
                "Sem1", _savedCgpa.value.sem1.toString(), _savedCgpa.value.earnCrSem1.toString()
            )
            _sem2.value = CgpaEditModel(
                "Sem2", _savedCgpa.value.sem2.toString(), _savedCgpa.value.earnCrSem2.toString()
            )
            _sem3.value = CgpaEditModel(
                "Sem3", _savedCgpa.value.sem3.toString(), _savedCgpa.value.earnCrSem3.toString()
            )
            _sem4.value = CgpaEditModel(
                "Sem4", _savedCgpa.value.sem4.toString(), _savedCgpa.value.earnCrSem4.toString()
            )
            _sem5.value = CgpaEditModel(
                "Sem5", _savedCgpa.value.sem5.toString(), _savedCgpa.value.earnCrSem5.toString()
            )
            _sem6.value = CgpaEditModel(
                "Sem6", _savedCgpa.value.sem6.toString(), _savedCgpa.value.earnCrSem6.toString()
            )

        }
    }
}