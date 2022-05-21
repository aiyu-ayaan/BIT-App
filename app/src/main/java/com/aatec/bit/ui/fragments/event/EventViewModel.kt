/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 1/22/22, 12:25 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 1/22/22, 11:51 AM
 */



package com.aatec.bit.ui.fragments.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aatec.bit.utils.MainStateEvent
import com.aatec.core.data.ui.event.Event
import com.aatec.core.data.ui.event.EventRepository
import com.aatec.core.utils.DataState
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
    private val _dataStateMain: MutableLiveData<DataState<List<Event>>> = MutableLiveData()
    val dataStateMain: LiveData<DataState<List<Event>>>
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