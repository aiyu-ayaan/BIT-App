package com.atech.bit.utils

import android.content.Context
import android.graphics.Color
import androidx.annotation.AttrRes
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.google.android.material.color.MaterialColors


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