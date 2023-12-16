package com.atech.bit.ui.screen.holiday

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.core.data_source.retrofit.model.Holiday
import com.atech.core.data_source.retrofit.model.HolidayType
import com.atech.core.use_case.KTorUseCase
import com.atech.view_model.OnErrorEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HolidayViewModel @Inject constructor(
    private val case: KTorUseCase
) : ViewModel() {
    private val _holidays = mutableStateOf<List<Holiday>>(emptyList())
    val holidays: State<List<Holiday>> get() = _holidays

    private val _oneTimeEvent = MutableSharedFlow<OnErrorEvent>()

    val oneTimeEvent = _oneTimeEvent


    fun onEvent(selectedTabIndex: Int) {
        if (selectedTabIndex == 1) getHolidays(HolidayType.RES)
        else getHolidays(HolidayType.ALL)
    }

    init {
        getHolidays()
    }

    private fun getHolidays(
        type: HolidayType = HolidayType.ALL
    ) = viewModelScope.launch {
        try {
            _holidays.value = case.fetchHolidays.invoke(type)
        } catch (e: Exception) {
            _oneTimeEvent.emit(OnErrorEvent.OnError(e.message ?: "Something went wrong"))
        }
    }
}