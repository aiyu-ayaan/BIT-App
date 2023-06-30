package com.atech.theme

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.atech.theme.databinding.LayoutRecyclerViewBinding
import com.google.android.material.color.MaterialColors
import java.text.SimpleDateFormat
import java.util.Date

enum class ToastLength(val length: Int) {
    SHORT(Toast.LENGTH_SHORT),
    LONG(Toast.LENGTH_LONG)
}

fun Fragment.toast(message: String, length: ToastLength = ToastLength.SHORT) =
    Toast.makeText(requireContext(), message, length.length).show()


fun LayoutRecyclerViewBinding.isLoadingDone(isLoadingComplete: Boolean) = this.apply {
    recyclerView.isVisible = isLoadingComplete
    empty.isVisible = !isLoadingComplete
}

fun getRgbFromHex(hex: String): String {
    val initColor = Color.parseColor(hex)
    val r = Color.red(initColor)
    val g = Color.green(initColor)
    val b = Color.blue(initColor)
    return "rgb($r,$g,$b)"
}

@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int
): Int {
    val typedArray = theme.obtainStyledAttributes(intArrayOf(attrColor))
    val textColor = typedArray.getColor(0, 0)
    typedArray.recycle()
    return textColor
}

fun getColorForText(context: Context) = context.run {
    if (this.isDark()) "rgb(255,255,255)"
    else "rgb(0,0,0)"
}

/**
 * Open Link
 * @param activity Current Activity
 * @author Ayaan
 * @since 4.0.2
// */
fun String.openLinks(activity: Activity, @StringRes string: Int) {
    try {
        activity.startActivity(Intent(Intent.ACTION_VIEW).also {
            it.data = Uri.parse(this)
        })
    } catch (e: Exception) {
        Toast.makeText(
            activity, activity.resources.getString(string), Toast.LENGTH_SHORT
        ).show()
    }
}

/**
 * Open Custom Tab
 * @since 4.0.4
 * @author Ayaan
 */
fun Context.openCustomChromeTab(link: String) = this.run {
    val defaultColors = CustomTabColorSchemeParams.Builder().setToolbarColor(
        MaterialColors.getColor(
            this, androidx.appcompat.R.attr.colorAccent, Color.RED
        )
    ).build()
    try {
        val customTabIntent =
            CustomTabsIntent.Builder().setDefaultColorSchemeParams(defaultColors).build()
        customTabIntent.intent.`package` = "com.android.chrome"
        customTabIntent.launchUrl(this, Uri.parse(link))
    } catch (e: Exception) {
        Toast.makeText(this, "Invalid Link", Toast.LENGTH_SHORT).show()
    }
}

/**
 * @since 4.0.3
 * @author Ayaan
 */
@SuppressLint("SimpleDateFormat")
fun Long.convertLongToTime(pattern: String): String = SimpleDateFormat(pattern).run {
    val date = Date(this@convertLongToTime)
    this.format(date)
}

fun <T> LinearLayout.addViews(
    activity: Activity,
    @LayoutRes id: Int,
    t: T,
    action: (T, View) -> Unit = { _, _ -> }

) =
    this.apply {
        val view = activity.layoutInflater.inflate(id, this, false)
        action(t, view)
        addView(view)
    }