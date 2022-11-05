/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 1/22/22, 12:25 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 1/22/22, 11:51 AM
 */



package com.atech.bit.ui.fragments.society

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.atech.core.api.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SocietyViewModel @Inject constructor(

    private val apiRepository: ApiRepository
) : ViewModel() {

    fun getSociety() = apiRepository.getSocietyData().asLiveData()
}