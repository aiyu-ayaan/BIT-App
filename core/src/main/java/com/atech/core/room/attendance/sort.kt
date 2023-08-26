package com.atech.core.room.attendance

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class SortOrder {
    ASC, DESC
}

enum class SortBy {
    SUBJECT, CREATED,
    TOTAL, PRESENT,
    PERCENTAGE
}

@Parcelize
data class Sort(
    val sortBy: SortBy = SortBy.SUBJECT,
    val sortOrder: SortOrder = SortOrder.ASC
) : Parcelable


