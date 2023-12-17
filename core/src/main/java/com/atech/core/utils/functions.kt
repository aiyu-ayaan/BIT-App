package com.atech.core.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
fun Long.convertLongToTime(pattern: String): String = SimpleDateFormat(pattern).run {
    val date = Date(this@convertLongToTime)
    this.format(date)
}

fun String.encodeUrlSpaces(): String = this.replace(" ", "%20")


fun Int.toBoolean() = this == 1


fun Number.ifZero(action: () -> Int): Number = if (this == 0) action() else this
