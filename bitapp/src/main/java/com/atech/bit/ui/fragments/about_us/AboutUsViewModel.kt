/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/23/22, 12:36 AM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/22/22, 11:26 PM
 */



package com.atech.bit.ui.fragments.about_us

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.atech.core.api.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class AboutUsViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val apiRepository: ApiRepository
) : ViewModel() {

    var aboutNestedViewPosition = MutableStateFlow(0)


    val aboutUsData = apiRepository.getAboutUsData().asLiveData()
}