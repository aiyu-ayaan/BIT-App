/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.core.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import java.util.Calendar
import java.util.TimeZone

/**
 * Created by Ayaan on 10/10/2019.
 *
 * @since 4.1.1 Patch 12
 */
object CalendarReminder {

    fun addEventAndReminderToCalendar(
        context: Context,
        calendar: Calendar,
        setContent: () -> Pair<String, String> = { DEFAULT_PAIR },
        action: (Long) -> Unit = {},
        error: (String) -> Unit = {}
    ) {
        try {
            var calID: Long = 3
            // get a available calendar id
            val projection = arrayOf(CalendarContract.Calendars._ID)
            val selection = CalendarContract.Calendars.VISIBLE + " = ?"
            val selectionArgs = arrayOf("1")
            val cursor = context.contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val calIDIndex = cursor.getColumnIndex(CalendarContract.Calendars._ID)
                calID = cursor.getLong(calIDIndex)
                cursor.close()
            }
            val startMillis: Long = Calendar.getInstance().run {
                set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    8,
                    30
                )
                timeInMillis
            }
            val endMillis: Long = Calendar.getInstance().run {
                set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    18,
                    0
                )
                timeInMillis
            }

            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, startMillis)
                put(CalendarContract.Events.DTEND, endMillis)
                put(CalendarContract.Events.TITLE, setContent().first)
                put(CalendarContract.Events.DESCRIPTION, setContent().second)
                put(CalendarContract.Events.CALENDAR_ID, calID)
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                put(CalendarContract.Events.ALL_DAY, false)
                put(CalendarContract.Events.HAS_ALARM, true)
                put(CalendarContract.Events.CUSTOM_APP_PACKAGE, context.packageName)

            }
            val uri: Uri? =
                context.contentResolver.insert(
                    CalendarContract.Events.CONTENT_URI,
                    values
                )
            val eventID: Long = uri?.lastPathSegment?.toLong() ?: -1
            // check event is added or not


            val reminderValues = ContentValues().apply {
                put(CalendarContract.Reminders.EVENT_ID, eventID)
                put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
                put(CalendarContract.Reminders.MINUTES, 15)
            }
            context.contentResolver.insert(
                CalendarContract.Reminders.CONTENT_URI,
                reminderValues
            )

            action.invoke(eventID)

        } catch (e: Exception) {
            error.invoke(
                "Error Occur !!" +
                        "\n$e"
            )
        }
    }

    fun updateEventAndReminder(
        context: Context,
        calendar: Calendar,
        eventID: Long,
        setContent: () -> Pair<String, String> = { DEFAULT_PAIR },
        action: () -> Unit = {},
        error: (String) -> Unit = {}
    ) {
        try {
            val startMillis: Long = Calendar.getInstance().run {
                set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    8,
                    30
                )
                timeInMillis
            }
            val endMillis: Long = Calendar.getInstance().run {
                set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    18,
                    0
                )
                timeInMillis
            }

            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, startMillis)
                put(CalendarContract.Events.DTEND, endMillis)
                put(CalendarContract.Events.TITLE, setContent().first)
                put(CalendarContract.Events.DESCRIPTION, setContent().second)
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                put(CalendarContract.Events.ALL_DAY, false)
                put(CalendarContract.Events.HAS_ALARM, true)
                put(CalendarContract.Events.CUSTOM_APP_PACKAGE, context.packageName)

            }
            val uri: Uri =
                ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID)
            context.contentResolver.update(uri, values, null, null)


            action.invoke()

        } catch (e: Exception) {
            error.invoke(e.message ?: "Error")
        }
    }


    fun deleteEvent(
        context: Context,
        eventID: Long,
        action: () -> Unit = {},
        error: (String) -> Unit = {}
    ) {
        try {
            val deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID)
            context.contentResolver.delete(deleteUri, null, null)
            action.invoke()
        } catch (e: Exception) {
            error.invoke(e.message ?: "Error")
        }
    }
}