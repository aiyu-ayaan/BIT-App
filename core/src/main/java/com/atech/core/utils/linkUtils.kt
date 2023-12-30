package com.atech.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent


const val PRIVACY_POLICY =
    "https://bit-lalpur-app.github.io/BIT-App-Data/privacy_polocy/privacy-policy"

fun Context.openPlayStore(name: String) {
    startActivity(
        Intent(
            Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$name")
        ).also {
            it.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
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


fun Context.openCustomChromeTab(link: String, color: Int) = this.run {
    val defaultColors = CustomTabColorSchemeParams.Builder().setToolbarColor(
        color
    ).build()
    try {
        val customTabIntent =
            CustomTabsIntent.Builder().setDefaultColorSchemeParams(defaultColors).build()
        customTabIntent.intent.`package` = "com.android.chrome"
        customTabIntent.launchUrl(this, Uri.parse(link))
    } catch (e: Exception) {
        Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
    }
}
