/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 3/17/22, 12:15 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 3/13/22, 10:35 AM
 */

package com.aatec.core.data.room.notice

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Keep
@Entity(tableName = "notice_3_table")
data class Notice3CacheEntity(
    @PrimaryKey(autoGenerate = false)
    val title: String,
    val body: String,
    val attach: String,
    val link: String,
    val sender: String,
    val path :String,
    val created: Date
)