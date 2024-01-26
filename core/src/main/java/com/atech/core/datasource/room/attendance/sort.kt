/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.core.datasource.room.attendance

import android.os.Parcelable
import androidx.annotation.Keep
import com.atech.core.datasource.room.attendance.SortBy.CREATED
import com.atech.core.datasource.room.attendance.SortBy.PERCENTAGE
import com.atech.core.datasource.room.attendance.SortBy.PRESENT
import com.atech.core.datasource.room.attendance.SortBy.SUBJECT
import com.atech.core.datasource.room.attendance.SortBy.TOTAL
import com.atech.core.datasource.room.attendance.SortOrder.ASC
import com.atech.core.datasource.room.attendance.SortOrder.DESC
import kotlinx.parcelize.Parcelize

/**
 * Model class for sortOrder in Attendance
 * Members [ASC], [DESC]
 */
@Keep
enum class SortOrder {
    ASC, DESC
}

/**
 * Model class for sortBy in Attendance
 * Members [SUBJECT], [CREATED], [TOTAL], [PRESENT], [PERCENTAGE]
 */
@Keep
enum class SortBy {
    SUBJECT, CREATED,
    TOTAL, PRESENT,
    PERCENTAGE
}

/**
 * Wrapper class for [SortBy] and [SortOrder]
 * @see com.atech.core.datasource.datastore.FilterPreferences
 * @see AttendanceModel
 */
@Keep
@Parcelize
data class Sort(
    val sortBy: SortBy = SortBy.SUBJECT,
    val sortOrder: SortOrder = SortOrder.ASC
) : Parcelable


