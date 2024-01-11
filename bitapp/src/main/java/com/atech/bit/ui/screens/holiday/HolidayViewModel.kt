/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.holiday

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.bit.utils.OnErrorEvent
import com.atech.core.datasource.firebase.remote.RemoteConfigHelper
import com.atech.core.datasource.retrofit.model.Holiday
import com.atech.core.datasource.retrofit.model.HolidayType
import com.atech.core.usecase.KTorUseCase
import com.atech.core.utils.RemoteConfigKeys
import com.atech.core.utils.TAGS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HolidayViewModel @Inject constructor(
    private val case: KTorUseCase,
    private val conf: RemoteConfigHelper,
) : ViewModel() {
    private val _holidays = mutableStateOf<List<Holiday>>(emptyList())
    val holidays: State<List<Holiday>> get() = _holidays

    private val _oneTimeEvent = MutableSharedFlow<OnErrorEvent>()

    val oneTimeEvent = _oneTimeEvent.asSharedFlow()

    private val _currentYear = mutableStateOf("")
    val currentYear: State<String> get() = _currentYear

    fun onEvent(selectedTabIndex: Int) {
        if (selectedTabIndex == 1) getHolidays(HolidayType.RES)
        else getHolidays(HolidayType.MAIN)
    }

    init {
        getHolidays()
        getConf()
    }

    private fun getConf() {
        conf.fetchData(failure = {
            Log.e(TAGS.BIT_REMOTE.name, "fetchRemoteConfigDetails: ${it.message}")
        }) {
            conf.getString(RemoteConfigKeys.CURRENT_YEAR.value).let {
                _currentYear.value = it
            }
        }
    }

    private fun getHolidays(
        type: HolidayType = HolidayType.MAIN
    ) = viewModelScope.launch {
        try {
            _holidays.value = case.fetchHolidays.invoke(type)
        } catch (e: Exception) {
            _oneTimeEvent.emit(OnErrorEvent.OnError(e.message ?: "Something went wrong"))
        }
    }
}