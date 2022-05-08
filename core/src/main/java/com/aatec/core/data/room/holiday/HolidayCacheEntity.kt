/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 2/21/22, 10:27 AM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 2/21/22, 10:22 AM
 */



package com.aatec.core.data.room.holiday

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
@Entity(tableName = "holiday_table")
data class HolidayCacheEntity(
    @PrimaryKey(autoGenerate = false)
    val sno: Int,
    val day: String,
    val date: String,
    val occasion: String,
    val month: String,
    val type: String
):Parcelable

