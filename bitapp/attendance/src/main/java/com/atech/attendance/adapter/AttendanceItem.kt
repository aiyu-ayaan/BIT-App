package com.atech.attendance.adapter

import com.atech.core.room.attendance.AttendanceModel
import com.atech.theme.AdsUnit

sealed class AttendanceItem {
    data class AttendanceData(val data: AttendanceModel) : AttendanceItem()

    data class Ads(val ads: AdsUnit) : AttendanceItem()
}