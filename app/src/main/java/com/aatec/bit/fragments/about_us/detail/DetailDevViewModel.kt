/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 2/21/22, 6:02 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 2/21/22, 12:43 PM
 */

package com.aatec.bit.fragments.about_us.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.aatec.core.data.network.aboutus.Devs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailDevViewModel @Inject constructor(
    private val state: SavedStateHandle
) : ViewModel() {

    var dev = state.get<Devs>("dev")
        set(value) {
            field = value
            state.set("dev", value)
        }
}