package com.atech.bit.ui.screens.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.core.usecase.DataStoreCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val dateStoreCase: DataStoreCases,
) : ViewModel() {
    private val _course = mutableStateOf("BCA")
    val course: State<String> get() = _course


    private val _sem = mutableStateOf("1")
    val sem: State<String> get() = _sem

    private var _dateStoreJob: Job? = null


    fun onEvent(event: LogInScreenEvents) {
        when (event) {
            is LogInScreenEvents.SaveCoursePref -> viewModelScope.launch{
                dateStoreCase.updateCourse(event.course)
                dateStoreCase.updateSem(event.sem)
            }
        }
    }

    init {
        getDataStore()
    }

    private fun getDataStore() {
        _dateStoreJob?.cancel()
        _dateStoreJob = dateStoreCase.getAll.invoke().onEach {
            _course.value = it.course
            _sem.value = it.sem
        }.launchIn(viewModelScope)
    }
}