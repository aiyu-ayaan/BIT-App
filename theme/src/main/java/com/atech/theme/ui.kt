package com.atech.theme

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.graphics.createBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.atech.theme.databinding.LayoutRecyclerViewBinding


fun Fragment.toast(message: String) =
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()


fun LayoutRecyclerViewBinding.isLoadingDone(isLoadingComplete : Boolean) =this.apply {
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