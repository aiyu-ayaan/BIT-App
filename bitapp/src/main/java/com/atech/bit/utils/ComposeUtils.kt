package com.atech.bit.utils

import android.content.Context
import android.graphics.Color
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.material.color.MaterialColors

object DPS {
    val GRID_0_5 = 4.dp
    val GRID_1 = 8.dp
    val GRID_2 = 16.dp
    val GRID_3 = 24.dp
    val GRID_4 = 32.dp
    val GRID_5 = 40.dp
    val GRID_6 = 48.dp
    val GRID_7 = 56.dp
    val GRID_8 = 64.dp
}

object BitAppTopologies {
    val captionTextAppearance = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.4.sp,
        lineHeight = 16.sp,
    )
}

fun Context.getComposeColor(
    @AttrRes id: Int
): androidx.compose.ui.graphics.Color = this.run {
    val color = MaterialColors.getColor(
        this,
        id,
        Color.WHITE
    )
    androidx.compose.ui.graphics.Color(color)
}