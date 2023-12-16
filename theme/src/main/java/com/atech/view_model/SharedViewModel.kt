package com.atech.view_model

import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {


    private val _isSearchActive = mutableStateOf(false)
    val isSearchActive: State<Boolean> get() = _isSearchActive

    private val _toggleDrawerState = mutableStateOf(null as DrawerValue?)
    val toggleDrawerState: State<DrawerValue?> get() = _toggleDrawerState

    private val _oneTimeEvent = MutableSharedFlow<MainScreenOneTimeEvent>()
    val oneTimeEvent = _oneTimeEvent.asSharedFlow()

    fun onEvent(event: SharedEvents) {
        when (event) {
            SharedEvents.ToggleSearchActive -> _isSearchActive.value = !_isSearchActive.value
            is SharedEvents.ToggleDrawer -> _toggleDrawerState.value = event.state
        }
    }


    fun onTimeEvent(event: MainScreenOneTimeEvent) = viewModelScope.launch {
        _oneTimeEvent.emit(event)
    }

    sealed class MainScreenOneTimeEvent {
        data class OpenLink(val link: String) : MainScreenOneTimeEvent()
    }

}