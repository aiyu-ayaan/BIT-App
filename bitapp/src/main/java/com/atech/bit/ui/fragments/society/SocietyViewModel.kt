/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 1/22/22, 12:25 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 1/22/22, 11:51 AM
 */



package com.atech.bit.ui.fragments.society

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.atech.bit.utils.MainStateEvent
import com.atech.core.data.network.society.SocietyNetworkEntity
import com.atech.core.data.network.society.SocietyRepository
import com.atech.core.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
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
    val dataState: Flow<DataState<List<SocietyNetworkEntity>>>
        get() = _dataState.asFlow()

    private val _dataStateNGOs: MutableLiveData<DataState<List<SocietyNetworkEntity>>> =
        MutableLiveData()
    val dataStateNGOs: Flow<DataState<List<SocietyNetworkEntity>>>
        get() = _dataStateNGOs.asFlow()

    fun setStateEvent(mainStateEvent: MainStateEvent) {
        when (mainStateEvent) {
            MainStateEvent.GetData -> {
                viewModelScope.launch {
                    repository.getSocieties().onEach { dataState ->
                        _dataState.value = dataState
                    }.launchIn(viewModelScope)

                    repository.getNGOs().onEach { dataState ->
                        _dataStateNGOs.value = dataState
                    }.launchIn(viewModelScope)
                }
            }
            MainStateEvent.NoInternet -> {
//                No one Cares
            }
        }
    }
}