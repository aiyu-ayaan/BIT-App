/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/25/22, 11:25 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/25/22, 10:30 PM
 */

package com.aatec.bit.ui.fragments.timetable

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.aatec.core.data.ui.timeTable.TimeTableModel
import com.aatec.core.data.ui.timeTable.TimeTableRepository
import com.aatec.core.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class TimeTableViewModel @Inject constructor(
    private val repository: TimeTableRepository
) : ViewModel() {

    private val _dataState: MutableLiveData<DataState<List<TimeTableModel>>> = MutableLiveData()
    val dataState: Flow<DataState<List<TimeTableModel>>> =
        _dataState.asFlow()

    val timeTable = repository.getTimeTable()

    val query = MutableStateFlow(QueryTimeTable())

    val defTimeTable = query.flatMapLatest { it ->
        repository.getDefault(it.course, it.gender, it.sem, it.sec)
    }
}

data class QueryTimeTable(
    val course: String = "",
    val gender: String = "",
    val sem: String = "",
    val sec: String = ""
)