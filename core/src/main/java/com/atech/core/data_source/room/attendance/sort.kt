package com.atech.core.data_source.room.attendance

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
enum class SortOrder {
    ASC, DESC
}

@Keep
enum class SortBy {
    SUBJECT, CREATED,
    TOTAL, PRESENT,
    PERCENTAGE
}

@Keep
@Parcelize
data class Sort(
    val sortBy: SortBy = SortBy.SUBJECT,
    val sortOrder: SortOrder = SortOrder.ASC
) : Parcelable


val attendanceList = listOf(
    AttendanceModel("Loc"),
    AttendanceModel("English", present = 12, total = 12),
    AttendanceModel("Maths", present = 34, total = 57),
    AttendanceModel("Science", present = 45, total = 56, teacher = "ABC"),
    AttendanceModel("Social", present = 56, total = 67, teacher = "XYZ"),
)

