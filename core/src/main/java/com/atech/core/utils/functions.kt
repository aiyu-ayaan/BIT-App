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


fun String.capitalizeWords(): String =
    lowercase().split(" ").joinToString(" ") {
        it.replaceFirstChar { it1 ->
            if (it1.isLowerCase()) it1.titlecase() else it
        }
    }
