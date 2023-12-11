package com.atech.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red


fun <T : Any> getSimpleName(clazz: Class<T>): String = clazz.simpleName.orEmpty()


fun Color.hexToRgb(): String {

    val red = this.toArgb().red
    val green = this.toArgb().green
    val blue = this.toArgb().blue
    return "rgb($red,$green,$blue)"
}