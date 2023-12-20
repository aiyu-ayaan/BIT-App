package com.atech.bit.ui.screens.holiday

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.bit.utils.OnErrorEvent
import com.atech.core.datasource.retrofit.model.Holiday
import com.atech.core.datasource.retrofit.model.HolidayType
import com.atech.core.usecase.KTorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HolidayViewModel @Inject constructor(
    private val case: KTorUseCase
) : ViewModel() {
    private val _holidays = mutableStateOf<List<Holiday>>(emptyList())
    val holidays: State<List<Holiday>> get() = _holidays

    private val _oneTimeEvent = MutableSharedFlow<OnErrorEvent>()

    val oneTimeEvent = _oneTimeEvent.asSharedFlow()


    fun onEvent(selectedTabIndex: Int) {
        if (selectedTabIndex == 1) getHolidays(HolidayType.RES)
        else getHolidays(HolidayType.MAIN)
    }

    init {
        getHolidays()
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