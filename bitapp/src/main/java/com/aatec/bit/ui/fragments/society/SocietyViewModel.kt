/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 1/22/22, 12:25 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 1/22/22, 11:51 AM
 */



package com.aatec.bit.ui.fragments.society

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aatec.bit.utils.MainStateEvent
import com.aatec.core.data.network.society.SocietyNetworkEntity
import com.aatec.core.data.network.society.SocietyRepository
import com.aatec.core.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocietyViewModel @Inject constructor(
    private val repository: SocietyRepository
) : ViewModel() {
    private val _dataState: MutableLiveData<DataState<List<SocietyNetworkEntity>>> =
        MutableLiveData()
    val dataState: LiveData<DataState<List<SocietyNetworkEntity>>>
        get() = _dataState

    val isColored = MutableStateFlow(false)

    fun setStateEvent(mainStateEvent: MainStateEvent) {
        when (mainStateEvent) {
            MainStateEvent.GetData -> {
                viewModelScope.launch {
                    repository.getSocieties().onEach { dataState ->
                        _dataState.value = dataState
                    }.launchIn(viewModelScope)
                }
            }
            MainStateEvent.NoInternet -> {
//                No one Cares
            }
        }
    }
}