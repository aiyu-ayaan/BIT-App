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
import com.atech.core.utils.AttendanceEvent
import com.atech.core.utils.convertDateToTime
import com.atech.core.utils.convertStringToLongMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CommunicatorViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val calendar: Calendar
) : ViewModel() {

    var isDataSet = state.get<Boolean>("isDataSet") ?: true
        set(value) {
            field = value
            state["isDataSet"] = value
        }

    var hasChange = state.get<Boolean>("hasChange") ?: false
        set(value) {
            field = value
            state["hasChange"] = value
        }

    val instanceBefore14Days = calendar.run {
        this.add(Calendar.DATE, -14)
        this.time.convertDateToTime().convertStringToLongMillis() //before 14 days
    }
    val instanceAfter15Days = calendar.run {
        this.add(Calendar.DATE, +15)
        this.time.convertDateToTime().convertStringToLongMillis() //Day after today
    }



    var homeNestedViewPosition = state.get<Int?>("homeNestedViewPosition")
        set(value) {
            field = value
            state["homeNestedViewPosition"] = value
        }



    val _attendanceEvent = Channel<AttendanceEvent>()
    val attendanceEvent = _attendanceEvent.receiveAsFlow()

    var maxTimeToUploadAttendanceData = state.get<Int>("maxTimeToUploadAttendanceData") ?: 0
        set(value) {
            field = value
            state["maxTimeToUploadAttendanceData"] = value
        }

    var uninstallDialogSeen = state.get<Boolean>("uninstallDialogSeen") ?: false
        set(value) {
            field = value
            state["uninstallDialogSeen"] = value
        }

    var attendanceManagerSize = state.get<Int>("attendanceManagerSize") ?: 0
        set(value) {
            field = value
            state["attendanceManagerSize"] = value
        }
}