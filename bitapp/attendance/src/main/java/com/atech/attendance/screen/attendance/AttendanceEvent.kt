package com.atech.attendance.screen.attendance

import com.atech.core.data_source.room.attendance.AttendanceModel

sealed class AttendanceEvent {
    data class ChangeAttendanceValue(
        val attendanceModel: AttendanceModel,
        val value: Int = 1,
        val isPresent: Boolean = true
    ) : AttendanceEvent()
}