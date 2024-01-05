package com.atech.core.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

fun Date.compareDifferenceInDays(date: Date): Int {
    val diff = this.time - date.time
    return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
}

infix fun Long.hasSameDay(date: Long): Boolean {
    val day1 =
        Calendar.getInstance().apply { timeInMillis = this@hasSameDay }.get(Calendar.DAY_OF_YEAR)
    val day2 = Calendar.getInstance().apply { timeInMillis = date }.get(Calendar.DAY_OF_YEAR)
    return day1 == day2
}

@SuppressLint("SimpleDateFormat")
fun Long.convertLongToTime(pattern: String): String = SimpleDateFormat(pattern).run {
    val date = Date(this@convertLongToTime)
    this.format(date)
}


fun Long.getDate(): String {
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
            " $minutes minutes ago"
        }

        hours < 24 -> {
            " $hours hours ago"
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
