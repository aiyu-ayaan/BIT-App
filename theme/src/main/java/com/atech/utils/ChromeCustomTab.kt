package com.atech.utils

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent

/**
 * Open Custom Tab
 * @since 4.0.4
 * @author Ayaan
 */
fun Context.openCustomChromeTab(
    link: String,
    color: Int = Color.GREEN
) = this.run {
    val defaultColors = CustomTabColorSchemeParams.Builder().setToolbarColor(color).build()
    try {
        val customTabIntent =
            CustomTabsIntent.Builder().setDefaultColorSchemeParams(defaultColors).build()
        customTabIntent.intent.`package` = "com.android.chrome"
        customTabIntent.launchUrl(this, Uri.parse(link))
    } catch (e: Exception) {
        Toast.makeText(this, "Invalid Link", Toast.LENGTH_SHORT).show()
    }
}