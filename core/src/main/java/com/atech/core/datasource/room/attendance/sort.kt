/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

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


