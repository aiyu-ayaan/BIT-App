/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/14/22, 2:16 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/13/22, 9:32 PM
 */



package com.atech.bit.ui.activity.main_activity.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.atech.core.data.preferences.Cgpa
import com.atech.core.data.preferences.PreferencesManager
import com.atech.core.data.room.syllabus.SyllabusDao
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

    fun updateCgpa(cgpa: Cgpa) = viewModelScope.launch {
        preferencesManager.updateCgpa(cgpa)
    }



    fun reset(openCode: String) = viewModelScope.launch {
        syllabusDao.reset(openCode)
    }

}