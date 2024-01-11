/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.cgpa

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.core.datasource.datastore.Cgpa
import com.atech.core.usecase.AuthUseCases
import com.atech.core.usecase.DataStoreCases
import com.atech.core.utils.TAGS
import com.atech.core.utils.UpdateDataType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CgpaViewModel @Inject constructor(
    private val case: DataStoreCases,
    private val authUseCases: AuthUseCases,
) : ViewModel() {

    private val _savedCgpa = mutableStateOf(Cgpa())

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

    private val _hasError = mutableStateOf(false)
    val hasError: State<Boolean> get() = _hasError
    private val _savedCalculateCGPA = mutableStateOf(1.0)
    val savedCalculateCGPA: State<Double> get() = _savedCalculateCGPA

    init {
        getAllDate()
    }


    fun onEvent(event: CGPAEvent) {
        when (event) {
            is CGPAEvent.OnSem1Change -> _sem1.value = _sem1.value.copy(
                semester = event.value.semester, earnCredit = event.value.earnCredit
            ).also {
                _hasError.value = checkHasError()
            }

            is CGPAEvent.OnSem2Change -> _sem2.value = _sem2.value.copy(
                semester = event.value.semester, earnCredit = event.value.earnCredit
            ).also {
                _hasError.value = checkHasError()
            }

            is CGPAEvent.OnSem3Change -> _sem3.value = _sem3.value.copy(
                semester = event.value.semester, earnCredit = event.value.earnCredit
            ).also {
                _hasError.value = checkHasError()
            }

            is CGPAEvent.OnSem4Change -> _sem4.value = _sem4.value.copy(
                semester = event.value.semester, earnCredit = event.value.earnCredit
            ).also {
                _hasError.value = checkHasError()
            }

            is CGPAEvent.OnSem5Change -> _sem5.value = _sem5.value.copy(
                semester = event.value.semester, earnCredit = event.value.earnCredit
            ).also {
                _hasError.value = checkHasError()
            }

            is CGPAEvent.OnSem6Change -> _sem6.value = _sem6.value.copy(
                semester = event.value.semester, earnCredit = event.value.earnCredit
            ).also {
                _hasError.value = checkHasError()
            }

            CGPAEvent.CalculateAndSave -> viewModelScope.launch {
                Log.d(
                    "AAA",
                    "onEvent: ${sem1.value.semester} ${sem1.value.semester.checkHaveValue()}"
                )
                if (
                    sem1.value.semester.checkHaveValue() &&
                    sem2.value.semester.checkHaveValue() &&
                    sem3.value.semester.checkHaveValue() &&
                    sem4.value.semester.checkHaveValue() &&
                    sem5.value.semester.checkHaveValue() &&
                    sem6.value.semester.checkHaveValue() &&
                    sem1.value.earnCredit.checkHaveValue() &&
                    sem2.value.earnCredit.checkHaveValue() &&
                    sem3.value.earnCredit.checkHaveValue() &&
                    sem4.value.earnCredit.checkHaveValue() &&
                    sem5.value.earnCredit.checkHaveValue() &&
                    sem6.value.earnCredit.checkHaveValue()
                ) {
                    _hasError.value = true
                    return@launch
                }
                val cgpa = Cgpa(
                    sem1 = sem1.value.semester.ifBlank { "0.0" }.toDouble(),
                    sem2 = sem2.value.semester.ifBlank { "0.0" }.toDouble(),
                    sem3 = sem3.value.semester.ifBlank { "0.0" }.toDouble(),
                    sem4 = sem4.value.semester.ifBlank { "0.0" }.toDouble(),
                    sem5 = sem5.value.semester.ifBlank { "0.0" }.toDouble(),
                    sem6 = sem6.value.semester.ifBlank { "0.0" }.toDouble(),
                    earnCrSem1 = sem1.value.earnCredit.ifBlank { "0.0" }.toDouble(),
                    earnCrSem2 = sem2.value.earnCredit.ifBlank { "0.0" }.toDouble(),
                    earnCrSem3 = sem3.value.earnCredit.ifBlank { "0.0" }.toDouble(),
                    earnCrSem4 = sem4.value.earnCredit.ifBlank { "0.0" }.toDouble(),
                    earnCrSem5 = sem5.value.earnCredit.ifBlank { "0.0" }.toDouble(),
                    earnCrSem6 = sem6.value.earnCredit.ifBlank { "0.0" }.toDouble(),
                    cgpa = _savedCgpa.value.cgpa,
                )
                val calculatedCGPA = cgpa.toPair().calculateCgpa()
                cgpa.copy(cgpa = calculatedCGPA).also {
                    case.updateCgpa.invoke(it)
                }
                uploadCgpa(
                    cgpa
                )
                _savedCalculateCGPA.value = calculatedCGPA
            }

            CGPAEvent.ClearAll -> viewModelScope.launch {
                case.updateCgpa.invoke(
                    Cgpa()
                )
                _sem1.value = CgpaEditModel()
                _sem2.value = CgpaEditModel()
                _sem3.value = CgpaEditModel()
                _sem4.value = CgpaEditModel()
                _sem5.value = CgpaEditModel()
                _sem6.value = CgpaEditModel()
                _hasError.value = false
            }
        }
    }

    private fun checkHasError() =
        _sem1.value.earnCredit.hasCreditError() ||
                _sem2.value.earnCredit.hasCreditError() ||
                _sem3.value.earnCredit.hasCreditError() ||
                _sem4.value.earnCredit.hasCreditError() ||
                _sem5.value.earnCredit.hasCreditError() ||
                _sem6.value.earnCredit.hasCreditError() ||
                _sem1.value.semester.hasCgpaError() ||
                _sem2.value.semester.hasCgpaError() ||
                _sem3.value.semester.hasCgpaError() ||
                _sem4.value.semester.hasCgpaError() ||
                _sem5.value.semester.hasCgpaError() ||
                _sem6.value.semester.hasCgpaError()


    private fun String.checkHaveValue(): Boolean =
        this.isEmpty() || this == "0.0"


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
            _savedCalculateCGPA.value = it.cgpa.cgpa
        }
    }

    private fun uploadCgpa(
        model: Cgpa
    ) = viewModelScope.launch {
        if (!authUseCases.hasLogIn.invoke()) return@launch
        try {
            authUseCases.uploadData.invoke(
                UpdateDataType.UpdateCgpa(
                    model
                )
            )
        } catch (e: Exception) {
            Log.e(TAGS.BIT_ERROR.name, "uploadCgpa: ${e.message ?: "Error"}")
        }
    }
}