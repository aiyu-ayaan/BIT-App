package com.atech.bit.utils

import android.graphics.Bitmap
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.palette.graphics.Palette

/**
 * Change Theme
 * @since 4.0.5
 * @author Ayaan
 */
fun isColorDark(@ColorInt color: Int): Boolean {
    val darkness: Double =
        1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
    return darkness >= 0.5
}

fun Bitmap.isDark(
    isDark: (Boolean) -> Unit
) = Palette.from(this).generate {
    it?.let {
        isColorDark(
            it.getDominantColor(
                Color.parseColor("#000000")
            )
        ).let(isDark)
    }
}