package com.atech.theme

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import com.google.android.material.color.MaterialColors

fun Context.isDark() =
    resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

fun Activity.changeStatusBarToolbarColorImageView(@ColorInt colorCode: Int) = this.apply {
    try {
        val window = window
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window?.statusBarColor = colorCode
    } catch (e: Exception) {
        Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
    }
}

fun Fragment.changeStatusBarToolbarColorImageView(@ColorInt colorCode: Int) = this.apply {
    try {
        val window = activity?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window?.statusBarColor = colorCode
    } catch (e: Exception) {
        Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Use to change status bar icons color
 */
@Suppress("DEPRECATION")
fun setStatusBarUiTheme(activity: Activity?, isLight: Boolean) {
    activity?.window?.decorView?.let {
        it.systemUiVisibility =
            if (isLight) it.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // dark icons
            else it.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() // light icons
    }
}

/**
 * Use to change bottom navigation bar color
 */
fun Activity.changeBottomNav(@AttrRes color: Int) = this.apply {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) window.navigationBarColor =
        MaterialColors.getColor(
            this, color, Color.RED
        )
}



/**
 * BottomNav Change color
 * @since 4.0.5
 * @author Ayaan
 */
fun Activity.changeBottomNavImageView(@ColorInt color: Int) = this.apply {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) window.navigationBarColor = color
}