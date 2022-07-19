/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 3/13/22, 10:32 AM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 3/12/22, 4:49 PM
 */



package com.atech.bit.ui.fragments.notice

import androidx.lifecycle.*
import com.atech.bit.utils.MainStateEvent
import com.atech.core.data.ui.notice.Notice3
import com.atech.core.data.ui.notice.NoticeRepository
import com.atech.core.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val repository: NoticeRepository,
) : ViewModel() {

    private val _dataStateNotice3Main: MutableLiveData<DataState<List<Notice3>>> = MutableLiveData()
    val dataStateNotice3Main: LiveData<DataState<List<Notice3>>>
        get() = _dataStateNotice3Main

    val type = MutableStateFlow("new")
    val isColored = MutableStateFlow(false)


    fun setStateListenerMain(mainStateEvent: MainStateEvent) {
        viewModelScope.launch {
            when (mainStateEvent) {
                MainStateEvent.GetData -> {
                    type.flatMapLatest { repository.getNotice3() }.onEach { dataState ->
                        _dataStateNotice3Main.value = dataState
                    }.launchIn(viewModelScope)
                }
                MainStateEvent.NoInternet -> {
                }
            }
        }
    }
}