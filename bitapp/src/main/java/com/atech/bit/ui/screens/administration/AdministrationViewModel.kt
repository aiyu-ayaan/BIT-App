/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.administration

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.bit.utils.OnErrorEvent
import com.atech.core.usecase.KTorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdministrationViewModel @Inject constructor(
    private val case: KTorUseCase
) : ViewModel() {
    private val _date = mutableStateOf("")
    val date: State<String> get() = _date

    private val _oneTimeEvent = MutableSharedFlow<OnErrorEvent>()

    val oneTimeEvent = _oneTimeEvent.asSharedFlow()

    init {
        initData()
    }

    private fun initData() = viewModelScope.launch {
        try {
            case.fetchAdministration().let { data ->
                _date.value = data
            }
        } catch (e: Exception) {
            _oneTimeEvent.emit(OnErrorEvent.OnError(e.message ?: "Something went wrong"))
        }
    }

}