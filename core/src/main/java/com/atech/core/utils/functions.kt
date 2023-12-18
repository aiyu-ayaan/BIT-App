package com.atech.core.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date


fun String.encodeUrlSpaces(): String = this.replace(" ", "%20")


fun Int.toBoolean() = this == 1


fun Number.ifZero(action: () -> Int): Number = if (this == 0) action() else this
