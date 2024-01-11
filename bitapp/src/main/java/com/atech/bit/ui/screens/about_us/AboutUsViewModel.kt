/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.about_us

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.core.datasource.retrofit.model.AboutUsModel
import com.atech.core.datasource.retrofit.model.Devs
import com.atech.core.usecase.KTorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AboutUsViewModel @Inject constructor(
    private val case: KTorUseCase
) : ViewModel() {
    private val empty = AboutUsModel(
        listOf(),
        listOf(),
        listOf()
    )
    private val _aboutUsModel = mutableStateOf(
        empty
    )
    val aboutUsModel: State<AboutUsModel> get() = _aboutUsModel


    private val _currentClickDev = mutableStateOf(Devs(-1, "", "", "", "", "", "", "", ""))
    val currentClickDev: State<Devs> get() = _currentClickDev

    init {
        getAboutUs()
    }

    private fun getAboutUs() = viewModelScope.launch {
        try {
            _aboutUsModel.value = case.fetchDevs()
        } catch (e: Exception) {
            _aboutUsModel.value = empty
        }
    }

    fun setCurrentClickDev(devs: Devs) {
        _currentClickDev.value = devs
    }
}