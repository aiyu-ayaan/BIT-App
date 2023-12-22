package com.atech.bit.ui.screens.login

import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.core.usecase.AuthUseCases
import com.atech.core.usecase.DataStoreCases
import com.atech.core.utils.SharePrefKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val dateStoreCase: DataStoreCases,
    val logInUseCase: AuthUseCases,
    private val pref: SharedPreferences
) : ViewModel() {
    private val _course = mutableStateOf("BCA")
    val course: State<String> get() = _course


    private val _sem = mutableStateOf("1")
    val sem: State<String> get() = _sem

    private var _dateStoreJob: Job? = null

    private val _logInState = mutableStateOf(LogInState())
    val logInState: State<LogInState> get() = _logInState

    private val _token = mutableStateOf<String?>(null)
    val token: State<String?> get() = _token

    private val _ui = mutableStateOf("")
    val ui: State<String> get() = _ui

    val hasSetUpDone = pref.getBoolean(SharePrefKeys.SetUpDone.name, false)

    fun updateSetUpDone(
        value: Boolean = false
    ) {
        pref.edit().apply {
            putBoolean(SharePrefKeys.SetUpDone.name, value)
        }.apply()
    }

    fun updateSkipLog(
        value: Boolean = false
    ) {
        pref.edit().apply {
            putBoolean(SharePrefKeys.PermanentSkipLogin.name, value)
        }.apply()
    }


    fun onEvent(event: LogInScreenEvents) {
        when (event) {
            is LogInScreenEvents.SaveCoursePref -> viewModelScope.launch {
                dateStoreCase.updateCourse(event.course)
                dateStoreCase.updateSem(event.sem)
            }

            is LogInScreenEvents.OnSignInResult -> _logInState.value = event.state
            LogInScreenEvents.SetUID -> _ui.value = logInUseCase.getUid() ?: ""
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