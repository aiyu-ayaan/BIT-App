/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 2/21/22, 10:27 AM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 2/20/22, 9:06 PM
 */

package com.atech.bit.ui.activity.main_activity.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.atech.bit.utils.AttendanceEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class CommunicatorViewModel @Inject constructor(
    private val state: SavedStateHandle
) : ViewModel() {
    var openFirst = state.get<Boolean>("openFirst") ?: true
        set(value) {
            field = value
            state.set("openFirst", value)
        }

    var homeNestedViewPosition = state.get<Int?>("homeNestedViewPosition")
        set(value) {
            field = value
            state.set("homeNestedViewPosition", value)
        }

    val query = MutableStateFlow("")


    val _attendanceEvent = Channel<AttendanceEvent>()
    val attendanceEvent = _attendanceEvent.receiveAsFlow()

    var uninstallDialogSeen = state.get<Boolean>("uninstallDialogSeen") ?: false
        set(value) {
            field = value
            state.set("uninstallDialogSeen", value)
        }
}