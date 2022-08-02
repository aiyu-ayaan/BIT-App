package com.atech.core.data.room.events

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.*

@Keep
@Entity(tableName = "events_table")
data class EventsCacheEntity(
    val created: Date,
    @PrimaryKey(autoGenerate = false)
    val title: String,
    var content: String,
    val insta_link: String,
    val logo_link: String,
    val path: String,
    val society: String,
    val video_link: String,
)

object DateConverter {
    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): Date? = if (null == value) null else Date(value)

    @TypeConverter
    @JvmStatic
    fun dateToTimestamp(date: Date?): Long? = date?.time
}