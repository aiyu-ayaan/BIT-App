package com.atech.bit.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red

fun Color.hexToRgb(): String {

    val red = this.toArgb().red
    val green = this.toArgb().green
    val blue = this.toArgb().blue
    return "rgb($red,$green,$blue)"
}

fun String.openLinks(context: Context) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW).also {
            it.data = Uri.parse(this)
        })
    } catch (e: Exception) {
        Toast.makeText(
            context, e.message, Toast.LENGTH_SHORT
        ).show()
    }
}
