package com.atech.attendance.adapter

import com.atech.core.room.attendance.AttendanceModel

sealed class AttendanceItem {
    data class AttendanceData(val data: AttendanceModel) : AttendanceItem()
}