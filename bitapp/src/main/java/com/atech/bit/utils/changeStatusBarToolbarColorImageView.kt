package com.atech.bit.utils

import android.app.Activity
import android.content.Context
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.runtime.Composable

@Composable
fun Context.changeStatusBarColor(colorCode: Int) = this.apply {
    try {
        this as Activity
        val window = window
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window?.statusBarColor = colorCode
    } catch (e: Exception) {
        Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
    }
}
