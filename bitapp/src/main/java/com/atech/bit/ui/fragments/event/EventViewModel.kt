/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 1/22/22, 12:25 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 1/22/22, 11:51 AM
 */



package com.atech.bit.ui.fragments.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.core.utils.MainStateEvent
import com.atech.core.data.ui.events.EventRepository
import com.atech.core.data.ui.events.Events
import com.atech.core.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {
    private val _dataStateMain: MutableLiveData<DataState<List<Events>>> = MutableLiveData()
    val dataStateMain: LiveData<DataState<List<Events>>>
        get() = _dataStateMain

    val isColored = MutableStateFlow(false)


    fun setStateListenerMain(mainStateEvent: MainStateEvent) {
        viewModelScope.launch {
            when (mainStateEvent) {
                MainStateEvent.GetData -> {
                    repository.getEvents()
                        .onEach { dataState ->
                            _dataStateMain.value = dataState
                        }.launchIn(viewModelScope)
                }
                MainStateEvent.NoInternet -> {
                }
            }
        }
    }
}