/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 2/8/22, 11:30 AM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 2/7/22, 10:26 AM
 */



package com.aatec.bit.ui.fragments.holiday

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.aatec.core.data.ui.holiday.HolidayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class HolidayViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val repository: HolidayRepository
) : ViewModel() {

    val query = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val holidays = query.flatMapLatest {
        repository.getHoliday(it)
    }
}