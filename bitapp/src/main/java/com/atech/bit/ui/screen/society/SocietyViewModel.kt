package com.atech.bit.ui.screen.society

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.core.data_source.retrofit.model.Society
import com.atech.core.use_case.KTorUseCase
import com.atech.view_model.OnErrorEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocietyViewModel @Inject constructor(
    private val case: KTorUseCase
) : ViewModel() {
    private val _fetchData =
        mutableStateOf<Pair<List<Society>, List<Society>>>(Pair(emptyList(), emptyList()))
    val fetchData: State<Pair<List<Society>, List<Society>>> get() = _fetchData

    private val _oneTimeEvent = MutableSharedFlow<OnErrorEvent>()
    val oneTimeEvent = _oneTimeEvent.asSharedFlow()

    init {
        fetchData()
    }

    private fun fetchData() = viewModelScope.launch {
        try {
            case.fetchSociety().let {
                _fetchData.value = it
            }
        } catch (e: Exception) {
            _oneTimeEvent.emit(OnErrorEvent.OnError(e.message.toString()))
        }
    }
}