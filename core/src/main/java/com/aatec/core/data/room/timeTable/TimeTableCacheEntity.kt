/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/3/22, 6:43 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/3/22, 6:43 PM
 */

package com.aatec.core.data.room.timeTable

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "time_table_table")
data class TimeTableCacheEntity(
    val course: String,
    val gender: String,
    val sem: String,
    val section: String,
    val imageLink : String,
    @PrimaryKey(autoGenerate = false)
    val created: Long
)