package com.atech.theme

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.atech.theme.databinding.LayoutRecyclerViewBinding
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

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

/**
 * @since 4.0.3
 * @author Ayaan
 */
fun View.showSnackBar(message: String, duration: Int) = Snackbar.make(
    this, message, duration
).apply {
    this.setBackgroundTint(
        MaterialColors.getColor(
            this.context, com.google.android.material.R.attr.colorSurface, Color.WHITE
        )
    )
    this.setTextColor(ContextCompat.getColor(this@showSnackBar.context, R.color.textColor))
}.show()


/**
 * @since 4.0.3
 * @author Ayaan
 */
fun View.showSnackBar(
    message: String, duration: Int, actionName: String?, action: (() -> Unit)?
) = Snackbar.make(
    this, message, duration
).apply {
    this.setBackgroundTint(
        MaterialColors.getColor(
            this.context, com.google.android.material.R.attr.colorSurface, Color.WHITE
        )
    )
    this.setTextColor(ContextCompat.getColor(this@showSnackBar.context, R.color.textColor))
    action?.let { action ->
        this.setActionTextColor(ContextCompat.getColor(this@showSnackBar.context, R.color.red))
        setAction(actionName) {
            action.invoke()
        }
    }
}.show()

fun Activity.openBugLink(
    @StringRes reportType: Int = R.string.bug_repost,
    extraString: String = "",
    reportDes: String? = null,
    version: String
) = this.startActivity(
    Intent.createChooser(
        Intent().also {
            it.putExtra(Intent.EXTRA_EMAIL, resources.getStringArray(R.array.email))
            it.putExtra(Intent.EXTRA_SUBJECT, resources.getString(reportType))
            it.putExtra(
                Intent.EXTRA_TEXT, if (extraString.isNotBlank()) """Found a bug on $extraString  
                                |happened on ${Date().time.convertLongToTime("dd/mm/yyyy hh:mm:aa")}
                                |due to $reportDes .
                                |
                                |Device Info: ${Build.MANUFACTURER} ${Build.MODEL}
                                |Android Version: ${Build.VERSION.RELEASE}
                                |App Version: $version
                                |""".trimMargin()
                else ""
            )
            it.type = "text/html"
            it.setPackage(resources.getString(R.string.gmail_package))
        }, resources.getString(R.string.bug_title)
    )
)

/**
 * This is only use in FragmentAddEdit.kt
 * @param type condition of attendance
 * @since 4.0.3
 * @author Ayaan
 */
fun String.getAndSetHint(type: String): Int = when (this) {
    type -> 0
    else -> this.toInt()
}

fun Long.getDate(): String {
    val currentTime = System.currentTimeMillis()
    val difference = currentTime - this
    val seconds = difference / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    val months = days / 30
    return when {
        seconds < 60 -> {
            " Just now"
        }

        minutes < 60 -> {
            " $minutes minutes ago"
        }

        hours < 24 -> {
            " $hours hours ago"
        }

        days < 30 -> {
            " ${this.convertLongToTime("dd MMM")}"
        }

        months < 12 -> {
            " ${this.convertLongToTime("dd MMM")}"
        }

        else -> {
            " ${this.convertLongToTime("dd MMM yyyy")}"
        }

    }
}

fun Context.openAppSettings() = this.apply {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri = Uri.fromParts("package", this.packageName, null)
    intent.data = uri
    this.startActivity(intent)
}

fun Date.compareDifferenceInDays(date: Date): Int {
    val diff = this.time - date.time
    return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
}

/**
 * Change Theme
 * @since 4.0.4
 * @author Ayaan
 */
fun setAppTheme(type: Int) {
    AppCompatDelegate.setDefaultNightMode(type)
}

fun ConstraintLayout.setHorizontalBias(
    @IdRes targetViewId: Int,
    verticalBias: Float,
    horizontalBias: Float = 0.0f

) {
    val constraintSet = ConstraintSet()
    constraintSet.clone(this)
    constraintSet.setHorizontalBias(targetViewId, horizontalBias)
    constraintSet.setVerticalBias(targetViewId, verticalBias)
    constraintSet.applyTo(this)
}

/**
 * Open Play Store
 * @author Ayaan
 * @since 4.0.5
 */
fun Activity.openPlayStore(name: String) {
    startActivity(Intent(
        Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$name")
    ).also {
        it.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    })
}

fun Fragment.openLinkToDefaultApp(link: String) = this.run {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    } catch (_: Exception) {
        requireContext().openCustomChromeTab(link)
    }
}