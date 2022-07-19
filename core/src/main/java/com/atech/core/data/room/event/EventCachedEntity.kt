/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 3/22/22, 10:45 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 3/22/22, 9:55 AM
 */



package com.atech.core.data.room.event

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Keep
@Entity(tableName = "event_table")
data class EventCachedEntity(
    val created: Date,
    val date: String,
    val event_body: String,
    @PrimaryKey(autoGenerate = false)
    val event_title: String,
    val ins_link: String,
    val logo_link: String,
    val society: String,
    val web_link: String,
    val poster_link: String?,
    val path: String?
) : Parcelable

object DateConverter {
    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): Date? = if (null == value) null else Date(value)

    @TypeConverter
    @JvmStatic
    fun dateToTimestamp(date: Date?): Long? = date?.time
}