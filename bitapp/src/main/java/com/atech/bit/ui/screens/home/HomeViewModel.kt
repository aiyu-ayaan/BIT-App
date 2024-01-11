/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.home

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.atech.bit.utils.SyllabusEnableModel
import com.atech.bit.utils.compareToCourseSem
import com.atech.core.datasource.retrofit.model.HolidayType
import com.atech.core.datasource.room.syllabus.SubjectType
import com.atech.core.usecase.AuthUseCases
import com.atech.core.usecase.DataStoreCases
import com.atech.core.usecase.FirebaseCase
import com.atech.core.usecase.KTorUseCase
import com.atech.core.usecase.SyllabusUseCase
import com.atech.core.utils.SYLLABUS_SOURCE_DATA
import com.atech.core.utils.SharePrefKeys
import com.atech.core.utils.TAGS
import com.atech.core.utils.UpdateDataType
import com.atech.core.utils.connectivity.ConnectivityObserver
import com.atech.core.utils.fromJSON
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val syllabusUSeCase: SyllabusUseCase,
    private val retrofitUseCase: KTorUseCase,
    private val dateStoreCase: DataStoreCases,
    val firebaseCase: FirebaseCase,
    private val pref: SharedPreferences,
    private val authUseCases: AuthUseCases,
    connectivityObserver: ConnectivityObserver,
    calendar: Calendar,
) : ViewModel() {

    val isConnected = connectivityObserver.observe()


    private val currentMonth = calendar.getDisplayName(
        Calendar.MONTH, Calendar.LONG, Locale.ENGLISH
    ) ?: "January"
    private val syllabusEnableModel: SyllabusEnableModel by lazy {
        pref.getString(
            SharePrefKeys.KeyToggleSyllabusSource.name, SYLLABUS_SOURCE_DATA
        )?.let {
            fromJSON(it, SyllabusEnableModel::class.java)!!
        } ?: fromJSON(
            SYLLABUS_SOURCE_DATA, SyllabusEnableModel::class.java
        )!!
    }
    private val _homeScreenState = mutableStateOf(HomeScreenState())
    val homeScreenState: State<HomeScreenState> get() = _homeScreenState

    private var _dateStoreJob: Job? = null

    private val _homeSearchScreenState = mutableStateOf(
        HomeScreechScreenState()
    )
    val homeSearchScreenState: State<HomeScreechScreenState> get() = _homeSearchScreenState


    fun onEvent(event: HomeScreenEvents) {
        when (event) {
            is HomeScreenEvents.ToggleOnlineSyllabusClick -> {
                _homeScreenState.value = _homeScreenState.value.copy(
                    isOnlineSyllabusEnable = event.isOnline
                )
            }

            is HomeScreenEvents.OnCourseChange -> {
                viewModelScope.launch {
                    dateStoreCase.updateCourse.invoke(event.value.first)
                    dateStoreCase.updateSem.invoke(event.value.second)
                    updateCourseSemInOnlineDatabase(
                        event.value.first, event.value.second
                    )
                }
            }
        }
    }


    //    ------------------------------------------ Fetching Data -------------------------------------


    init {
        getDataStore()
    }

    private fun getDataStore() {
        _dateStoreJob?.cancel()
        _dateStoreJob = dateStoreCase.getAll.invoke().onEach {
            val event = getEvent()
            _homeScreenState.value = _homeScreenState.value.copy(
                course = it.course,
                sem = it.sem,
                currentCgpa = it.cgpa,
                isOnlineSyllabusEnable = syllabusEnableModel.compareToCourseSem(
                    it.course + it.sem
                ),
                offTheory = syllabusUSeCase.getSubjectsByType(
                    courseSem = "${it.course}${it.sem}".lowercase(), type = SubjectType.THEORY
                ).cachedIn(viewModelScope).stateIn(scope = viewModelScope),
                offLab = syllabusUSeCase.getSubjectsByType(
                    courseSem = "${it.course}${it.sem}".lowercase(), type = SubjectType.LAB
                ).cachedIn(viewModelScope).stateIn(scope = viewModelScope),
                offPractical = syllabusUSeCase.getSubjectsByType(
                    courseSem = "${it.course}${it.sem}".lowercase(), type = SubjectType.PE
                ).cachedIn(viewModelScope).stateIn(scope = viewModelScope),
                onlineSyllabus = try {
                    retrofitUseCase.fetchSyllabus("${it.course}${it.sem}".lowercase())
                } catch (e: Exception) {
                    Triple(emptyList(), emptyList(), emptyList())
                },
                holiday = getHoliday(query = currentMonth),
                events = event
            )
        }.launchIn(viewModelScope)
    }

    private suspend fun getEvent(
        query: String? = null
    ) = try {
        firebaseCase.getEvent().let { filter ->
            if (query == null) filter
            else filter.map {
                it.filter { event ->
                    event.title?.contains(query, ignoreCase = true) ?: false
                }
            }
        }.stateIn(scope = viewModelScope)
    } catch (e: Exception) {
        MutableStateFlow(emptyList())
    }

    private suspend fun getHoliday(
        type: HolidayType = HolidayType.ALL, query: String
    ) = try {
        retrofitUseCase.fetchHolidays.invoke(
            type, query = query
        )
    } catch (e: Exception) {
        emptyList()
    }


    private fun updateCourseSemInOnlineDatabase(
        course: String, sem: String
    ) = viewModelScope.launch {
        if (!authUseCases.hasLogIn.invoke()) return@launch
        try {
            authUseCases.uploadData.invoke(
                UpdateDataType.UpdateCourseSem(
                    course = course, sem = sem
                )
            )
        } catch (e: Exception) {
            Log.e(TAGS.BIT_ERROR.name, "uploadCgpa: ${e.message ?: "Error"}")
        }
    }
//    ------------------------------------------ Searching -----------------------------------------

    fun getSearchItem(query: String) {
        if (query.isBlank()) {
            _homeSearchScreenState.value = HomeScreechScreenState()
            return
        }
        viewModelScope.launch {
            _homeSearchScreenState.value = _homeSearchScreenState.value.copy(
                offSyllabus = syllabusUSeCase.getSearchSyllabus(query).cachedIn(viewModelScope)
                    .stateIn(scope = viewModelScope),
                holiday = getHoliday(HolidayType.SEARCH, query = query),
                events = getEvent(query = query)
            )
        }
    }
}