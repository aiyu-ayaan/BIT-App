/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/14/22, 2:16 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/13/22, 9:32 PM
 */



package com.aatec.bit.ui.activity.main_activity.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.aatec.core.data.preferences.PreferencesManager
import com.aatec.core.data.room.syllabus.SyllabusDao
import com.aatec.core.utils.Gender
import com.aatec.core.utils.Section
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferenceManagerViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val syllabusDao: SyllabusDao
) : ViewModel() {
    private val _preferencesFlow = preferencesManager.preferencesFlow
    val preferencesFlow get() = _preferencesFlow.asLiveData()


    fun updatePercentage(defPercentage: Int) = viewModelScope.launch {
        preferencesManager.updatePercentage(defPercentage)
    }


    fun updateCourse(value: String) = viewModelScope.launch {
        preferencesManager.updateCourse(value)
    }

    fun updateSem(value: String) = viewModelScope.launch {
        preferencesManager.updateSem(value)
    }

    fun updateSemSyllabus(value: String) = viewModelScope.launch {
        preferencesManager.updateSemSyllabus(value)
    }

    fun updateSearchSetting(
        event: Boolean,
        holiday: Boolean,
        notice: Boolean,
        subject: Boolean
    ) = viewModelScope.launch {
        preferencesManager.updateSearchSetting(event, holiday, notice, subject)
    }


    fun updateTimeTableSetting(
        course: String,
        gender: Gender,
        sem: String,
        section: Section
    ) = viewModelScope.launch {
        preferencesManager.updateTimeTableSettings(course, gender, sem, section)
    }

    fun updateTimeTableOpen(value: Boolean) = viewModelScope.launch {
        preferencesManager.updateFirstTimeTableOpen(value)
    }

    fun reset(openCode: String) = viewModelScope.launch {
        syllabusDao.reset(openCode)
    }

}