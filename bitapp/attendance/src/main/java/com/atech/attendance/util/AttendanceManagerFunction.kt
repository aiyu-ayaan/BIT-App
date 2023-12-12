/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/1/21, 1:09 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/1/21, 11:14 AM
 */



package com.atech.attendance.util

import com.atech.core.utils.convertLongToTime

fun findPercentage(present: Float, total: Float, action: (Float, Float) -> Float) =
    action(present, total)

fun setResources(percentage: Int, action: (Int) -> Unit) =
    action(percentage)

fun calculatedDays(present: Int, total: Int, action: (Float, Float) -> Float) =
    action(present.toFloat(), total.toFloat())


fun Long.getRelativeDateForAttendance(): String {
    val currentTime = System.currentTimeMillis()
    val difference = currentTime - this
    val seconds = difference / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    val months = days / 30

    return when {
        seconds < 60 -> {
            " Just now"
        }

        minutes < 60 -> {
            " ${minutes.toInt()} minute${if (minutes.toInt() > 1) "s" else ""} ago"
        }

        hours < 24 -> {
            " ${hours.toInt()} hour${if (hours.toInt() > 1) "s" else ""} ago"
        }

        days == 1L -> {
            " Yesterday"
        }

        days < 30 -> {
            " ${this.convertLongToTime("dd MMM")}"
        }

        months < 12 -> {
            " ${this.convertLongToTime("dd MMM")}"
        }

        else -> {
            " ${this.convertLongToTime("dd MMM yyyy")}"
        }
    }
}


