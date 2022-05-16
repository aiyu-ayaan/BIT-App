/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/23/22, 12:36 AM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/22/22, 11:26 PM
 */



package com.aatec.bit.fragments.about_us

import androidx.lifecycle.*
import com.aatec.bit.utils.MainStateEvent
import com.aatec.core.data.network.aboutus.Devs
import com.aatec.core.data.ui.aboutUs.AboutUsRepository
import com.aatec.core.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutUsViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val repository: AboutUsRepository
) : ViewModel() {
    private val _dataState: MutableLiveData<DataState<List<Devs>>> = MutableLiveData()
    val dataState: Flow<DataState<List<Devs>>>
        get() = _dataState.asFlow()

    private val _dataStateContributors: MutableLiveData<DataState<List<Devs>>> = MutableLiveData()
    val dataStateContributors: Flow<DataState<List<Devs>>>
        get() = _dataStateContributors.asFlow()


    var aboutNestedViewPosition = MutableStateFlow(0)

    fun setStateListener(mainStateEvent: MainStateEvent) {
        viewModelScope.launch {
            when (mainStateEvent) {
                MainStateEvent.GetData -> {
                    repository.getDevs().onEach { dataState ->
                        _dataState.value = dataState
                    }.launchIn(viewModelScope)
                    repository.getContributors().onEach { dataState ->
                        _dataStateContributors.value = dataState
                    }.launchIn(viewModelScope)
                }
                MainStateEvent.NoInternet -> {


                }
            }
        }
    }
}