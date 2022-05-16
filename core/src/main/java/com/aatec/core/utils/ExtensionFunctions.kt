/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/14/22, 2:16 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/13/22, 8:55 PM
 */



package com.aatec.bit.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.recyclerview.widget.RecyclerView
import com.aatec.core.R
import com.aatec.core.data.room.attendance.AttendanceModel
import com.aatec.core.data.room.attendance.IsPresent
import com.aatec.core.data.ui.notice.Notice3
import com.aatec.core.utils.DEFAULT_CORNER_RADIUS
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


inline fun NavController.onDestinationChange(crossinline des: ((NavDestination) -> Unit)) =
    this.addOnDestinationChangedListener { _, destination, _ ->
        des.invoke(destination)
    }

/**
 * Open Link
 * @param activity Current Activity
 * @author Ayaan
 * @since 4.0.2
// */
fun String.openLinks(activity: Activity, @StringRes string: Int) {
    try {
        activity.startActivity(
            Intent(Intent.ACTION_VIEW).also {
                it.data = Uri.parse(this)
            }
        )
    } catch (e: Exception) {
        Toast.makeText(
            activity,
            activity.resources.getString(string),
            Toast.LENGTH_SHORT
        ).show()
    }
}

/**
 * Load Image in ImageView in
 * @param parentView Parent View or Context
 * @param view ImageView
 * @param progressBar ProgressBar
 * @author Ayaan
 * @since 4.0.2
 */
fun String.loadImageCircular(
    parentView: View,
    view: ImageView,
    progressBar: ProgressBar,
    @DrawableRes errorImage: Int
) =
    Glide.with(parentView)
        .load(this)
        .centerCrop()
        .apply(RequestOptions().circleCrop())
        .error(errorImage)
        .transition(DrawableTransitionOptions.withCrossFade())
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                progressBar.visibility = View.GONE
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                progressBar.visibility = View.GONE
                return false
            }

        })
        .timeout(10000)
        .into(view)


/**
 * Load image in ImageView
 * @param parentView Parent View or Context
 * @param view ImageView
 * @param progressBar ProgressBar (Nullable)
 * @author Ayaan
 * @since 4.0.2
 */
fun String.loadImage(
    parentView: View,
    view: ImageView,
    progressBar: ProgressBar?,
    cornerRadius: Int = DEFAULT_CORNER_RADIUS,
    @DrawableRes errorImage: Int
) =
    Glide.with(parentView)
        .load(this)
        .fitCenter()
        .error(errorImage)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.visibility = View.GONE
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.visibility = View.GONE
                return false
            }

        })
        .apply(com.bumptech.glide.request.RequestOptions.bitmapTransform(RoundedCorners(cornerRadius)))
        .timeout(10000)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(view)


fun String.loadImageDefault(
    parentView: View,
    view: ImageView,
    progressBar: ProgressBar?,
    @DrawableRes errorImage: Int
) = this.apply {
    Glide.with(parentView)
        .load(this)
        .error(errorImage)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.visibility = View.GONE
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                progressBar?.visibility = View.GONE

                return false
            }

        })
        .timeout(6000)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(view)
}

fun String.loadImageBitMap(
    parentView: View,
    @DrawableRes errorImage: Int,
    customAction: ((Bitmap?) -> Unit)? = null,
) {
    Glide.with(parentView)
        .asBitmap()
        .load(this@loadImageBitMap)
        .error(errorImage)
        .listener(object : RequestListener<Bitmap> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Bitmap>?,
                isFirstResource: Boolean
            ): Boolean = false


            override fun onResourceReady(
                resource: Bitmap?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Bitmap>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                customAction?.invoke(resource)
                return false
            }

        }).submit(100, 100)
}
//
/**
 * Extension function to open Compose Activity
 * @author Ayaan
 * @since 4.0.3
 */
fun Activity.openBugLink() =
    this.startActivity(
        Intent.createChooser(
            Intent()
                .also {
                    it.putExtra(Intent.EXTRA_EMAIL, resources.getStringArray(R.array.email))
                    it.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.bug_repost))
                    it.type = "text/html"
                    it.setPackage(resources.getString(R.string.gmail_package))
                }, resources.getString(R.string.bug_title)
        )
    )


/**
 *Extension function to share link
 * @author Ayaan
 * @since 4.0.3
 */
fun Activity.openShareLink() =
    this.startActivity(Intent.createChooser(Intent().apply {

        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT, "${resources.getString(R.string.app_share_content)} \n" +
                    "${resources.getString(R.string.play_store_link)}$packageName"
        )
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, resources.getString(R.string.share_app))

        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }, null))
//
/**
 *Extension function to open Share
 * @author Ayaan
 * @since 4.0.3
 */
fun Activity.openShareDeepLink(title: String, path: String) =
    this.startActivity(Intent.createChooser(Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT, """
            $title .
            Link: ${
                Uri.parse(
                    resources.getString(R.string.deep_link_share_link, path.trim())
                )
            }

            Sauce: ${resources.getString(R.string.play_store_link)}$packageName
        """.trimIndent()
        )
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, title)

        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }, null))


/**
 * Replace all \n or \r to br
 * @return
 * @since 2.0
 */
fun String.replaceNewLineWithBreak() =
    this.replace("\n|\r\n".toRegex(), "<br>")

/**
 * Convert valid link into Bitmap use applyImageUrl()
 * @return Bitmap
 * @throws IOException unused
 * @author Ayaan
 * @since 4.0.2
 * @see com.aatec.bit.utils.applyImageUrl()
 * @deprecated This Function is Deprecated. Use {@link com.aatec.bit.utils.applyImageUrl()}
 */
fun String.converterLinkToBitmap(): Bitmap? =
    runBlocking(Dispatchers.IO) {
        try {
            val httpURLConnection = URL(this@converterLinkToBitmap).openConnection()
                    as HttpURLConnection
            httpURLConnection.doInput = true
            httpURLConnection.connect()
            BitmapFactory.decodeStream(httpURLConnection.inputStream)
        } catch (unused: IOException) {
            null
        }
    }

/**
 *Covert valid link into Bitmap in background thread
 * @return Notification Builder
 * @throws IOException unused
 * @author Ayaan
 * @since 4.0.3
 * @see com.aatec.bit.Service.FcmService
 */
fun String.applyImageUrl(builder: NotificationCompat.Builder) =
    runBlocking(Dispatchers.IO) {
        val url = URL(this@applyImageUrl)
        withContext(Dispatchers.IO) {
            try {
                val input = url.openStream()
                BitmapFactory.decodeStream(input)
            } catch (e: IOException) {
                null
            }
        }?.let { bitmap ->
            builder.setLargeIcon(bitmap)
            builder.setStyle(
                NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null)
            )
        }
    }

/**
 * @since 4.0.3
 * @author Ayaan
 */
@SuppressLint("SimpleDateFormat")
fun Date.convertDateToTime(): String =
    SimpleDateFormat("dd/MM/yyyy").format(this)

/**
 * @since 4.0.3
 * @author Ayaan
 */
@SuppressLint("SimpleDateFormat")
fun Long.convertLongToTime(pattern: String): String =
    SimpleDateFormat(pattern).run {
        val date = Date(this@convertLongToTime)
        this.format(date)
    }


/**
 * @since 4.0.3
 * @author Ayaan
 */
@SuppressLint("SimpleDateFormat")
fun String.convertStringToLongMillis(): Long? =
    SimpleDateFormat("dd/MM/yyyy").run {
        try {
            val d = this.parse(this@convertStringToLongMillis)
            d?.time
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

/**
 * @since 4.0.3
 * @author Ayaan
 */
inline fun AttendanceModel.showUndoMessage(
    parentView: View,
    crossinline action: (AttendanceModel) -> Unit
) =
    Snackbar.make(
        parentView,
        "Deleted ${this.subject}",
        Snackbar.LENGTH_SHORT
    ).setAction("Undo") {
        action.invoke(this)
    }.apply {
        this.setBackgroundTint(ContextCompat.getColor(parentView.context, R.color.cardBackground))
        this.setActionTextColor(ContextCompat.getColor(parentView.context, R.color.red))
        this.setTextColor(ContextCompat.getColor(parentView.context, R.color.textColor))
    }.show()


/**
 * @since 4.0.3
 * @author Ayaan
 */
fun View.showSnackBar(message: String, duration: Int) =
    Snackbar.make(
        this,
        message,
        duration
    ).apply {
        this.setBackgroundTint(
            ContextCompat.getColor(
                this@showSnackBar.context,
                R.color.cardBackground
            )
        )
        this.setTextColor(ContextCompat.getColor(this@showSnackBar.context, R.color.textColor))
    }.show()


/**
 * @since 4.0.3
 * @author Ayaan
 */
fun View.showSnackBar(
    message: String,
    duration: Int,
    actionName: String?,
    action: (() -> Unit)?
) =
    Snackbar.make(
        this,
        message,
        duration
    ).apply {
        this.setBackgroundTint(
            ContextCompat.getColor(
                this@showSnackBar.context,
                R.color.cardBackground
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


/**
 * @since 4.0.3
 * @author Ayaan
 */
fun ArrayList<IsPresent>.countTotalClass(size: Int, isPresent: Boolean): Int {
    var days = 1
    val removeIndex = arrayListOf<Int>()
    for ((index, i) in this.withIndex()) {
        if (
            this.last().day.convertLongToTime("dd/mm/yyyy") == i.day.convertLongToTime("dd/mm/yyyy") &&
            i.isPresent == isPresent
        ) {
            days++
            if (size - 1 != index) {
                removeIndex.add(index)
            }
        }
    }
    for (r in removeIndex.reversed()) {
        this.removeAt(r)
    }
    return days
}


/**
 * This is only use in FragmentAddEdit.kt
 * @param type condition of attendance
 * @since 4.0.3
 * @author Ayaan
 * @see com.aatec.bit.ui.Fragments.Attendance.Dialog.BottomSheet.AddEdit.AddSubjectBottomSheet
 */
fun String.getAndSetHint(type: String): Int =
    when (this) {
        type -> 0
        else -> this.toInt()
    }

/**
 * Detect current theme
 * @since 4.0.3
 * @author Ayaan
 *
 */
fun Context.isDark() =
    resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES


/**
 * Return color in hex for webView
 * @param context Current Context
 * @author Ayaan
 * @since 4.0.3
 */
fun getColorForBackground(context: Context) = context.run {
    if (this.isDark()) "rgb(23, 26, 31)"
    else "rgb(255,255,255)"
}

/**
 * Return color in hex for webView
 * @param context Current Context
 * @author Ayaan
 * @since 4.0.3
 */
fun getColorForText(context: Context) = context.run {
    if (this.isDark()) "rgb(255,255,255)"
    else "rgb(0,0,0)"
}


///**
// * Custom Back Pressed
// * @since 4.0.3
// * @author Ayaan
// */
//fun Fragment.handleCustomBackPressed(
//    onBackPressed: OnBackPressedCallback.() -> Unit
//) {
//    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
//        onBackPressed()
//    }
//}

/**
 * Recycler View
 * @since 4.0.4
 * @author Ayaan
 */
inline fun RecyclerView.onScrollChange(
    crossinline lambda1: (RecyclerView) -> Unit,
    crossinline lambda2: (RecyclerView) -> Unit
) =
    this.addOnScrollListener(
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0)
                    lambda1.invoke(recyclerView)
                else if (dy < 0)
                    lambda2.invoke(recyclerView)
            }
        }
    )


/**
 * Change Color
 * @since 4.0.4
 * @author Ayaan
// */
fun Activity.changeStatusBarToolbarColor(@IdRes id: Int, @AttrRes colorCode: Int) =
    this.apply {
        try {
            val window = window
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.statusBarColor = MaterialColors.getColor(
                this,
                colorCode,
                Color.WHITE
            )
            this.findViewById<Toolbar>(id).setBackgroundColor(
                MaterialColors.getColor(
                    this,
                    colorCode,
                    Color.WHITE
                )
            )
        } catch (e: Exception) {
            Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

/**
 * Change Color of ImageView
 * @since 4.0.5
 * @author Ayaan
 */
fun Activity.changeStatusBarToolbarColorImageView(@ColorInt colorCode: Int) =
    this.apply {
        try {
            val window = window
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.statusBarColor = colorCode
        } catch (e: Exception) {
            Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

/**
 * Change Status Bar Color
 * @since 4.0.4
 * @author Ayaan
 */
@Suppress("DEPRECATION")
fun setStatusBarUiTheme(activity: Activity?, isLight: Boolean) {
    activity?.window?.decorView?.let {
        it.systemUiVisibility = if (isLight)
            it.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // dark icons
        else
            it.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() // light icons
    }
}

/**
 * Provide android pending intent
 * @since 4.0.4
 * @author Ayaan
 */
fun getPendingIntentFlag() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_ONE_SHOT


/**
 * Open Custom Tab
 * @since 4.0.4
 * @author Ayaan
 */
fun Context.openCustomChromeTab(link: String) = this.run {
    val defaultColors = CustomTabColorSchemeParams.Builder()
        .setToolbarColor(
            MaterialColors.getColor(
                this,
                androidx.appcompat.R.attr.colorAccent,
                Color.RED
            )
        )
        .build()
    val customTabIntent =
        CustomTabsIntent.Builder().setDefaultColorSchemeParams(defaultColors).build()
    customTabIntent.intent.`package` = "com.android.chrome"
    customTabIntent.launchUrl(this, Uri.parse(link))
}

/**
 * BottomNav Change color
 * @since 4.0.4
 * @author Ayaan
 */
fun Activity.changeBottomNav(@AttrRes color: Int) = this.apply {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
        window.navigationBarColor = MaterialColors.getColor(
            this,
            color,
            Color.RED
        )
}

fun Activity.changeBottomNavColor(@ColorInt color: Int) = this.apply {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
        window.navigationBarColor = color
}

/**
 * BottomNav Change color
 * @since 4.0.5
 * @author Ayaan
 */
fun Activity.changeBottomNavImageView(@ColorInt color: Int) = this.apply {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
        window.navigationBarColor = color
}


/**
 * Change Theme
 * @since 4.0.4
 * @author Ayaan
 */
fun setAppTheme(type: Int) {
    AppCompatDelegate.setDefaultNightMode(type)
}

/**
 * Change Theme
 * @since 4.0.5
 * @author Ayaan
 */
fun isColorDark(@ColorInt color: Int): Boolean {
    val darkness: Double =
        1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
    return darkness >= 0.5
}


/**
 *Extension function to open Share
 * @author Ayaan
 * @since 4.0.5
 */
@Suppress("DEPRECATION")
fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path: String =
        MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "{${System.currentTimeMillis()}}",
            null
        )
    return Uri.parse(path)
}


/**
 *Extension function to open Share
 * @author Ayaan
 * @since 4.0.5
 */
fun Activity.openShareImageDeepLink(
    context: Context,
    title: String,
    path: String,
    imageLink: String,
    share_type: String = "event"
) =
    this.startActivity(Intent.createChooser(Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, getImageUri(context, imageLink.converterLinkToBitmap()!!))
        putExtra(
            Intent.EXTRA_TEXT, """
            $title .
            Link: ${
                Uri.parse(
                    when (share_type) {
                        "event" -> resources.getString(
                            R.string.deep_link_share_event_link,
                            path.trim()
                        )
                        else -> resources.getString(R.string.deep_link_share_link, path.trim())
                    }
                )
            }

            Sauce: ${resources.getString(R.string.play_store_link)}$packageName
        """.trimIndent()
        )
        type = "image/*"
        putExtra(Intent.EXTRA_TITLE, title)

        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }, null))

//
//
///**
// * Get Link for Notification type
// * @author Ayaan
// * @since 4.0.5
// */
fun Notice3.getImageLinkNotification(context: Context): String =
    when (this.sender) {
        "App Notice" -> "https://firebasestorage.googleapis.com/v0/b/theaiyubit.appspot.com/o/Utils%2Fapp_notification.png?alt=media&token=0a7babfe-bf59-4d19-8fc0-98d7fde151a6"
        else -> "https://firebasestorage.googleapis.com/v0/b/theaiyubit.appspot.com/o/Utils%2Fcollege_notifications.png?alt=media&token=c5bbfda0-c73d-4af1-9c3c-cb29a99d126b"
    }


/**
 * Open Play Store
 * @author Ayaan
 * @since 4.0.5
 */
fun Activity.openPlayStore(name: String = packageName) {
    startActivity(
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=$name")
        ).also {
            it.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    )
}

inline fun Activity.onScrollColorChange(
    scrollView: NestedScrollView,
    crossinline to: (() -> Unit),
    crossinline from: (() -> Unit)
) =
    scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
        when (scrollY) {
            0 -> {
                to.invoke()
            }
            else -> {
                from.invoke()
            }
        }
    }

fun <T1, T2, T3, T4, T5, T6, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    transform: suspend (T1, T2, T3, T4, T5, T6) -> R
): Flow<R> = combine(
    combine(flow, flow2, flow3, ::Triple),
    combine(flow4, flow5, flow6, ::Triple)
) { t1, t2 ->
    transform(
        t1.first,
        t1.second,
        t1.third,
        t2.first,
        t2.second,
        t2.third
    )
}

fun <T1, T2, T3, T4, R> combineFourFlows(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    transform: suspend (T1, T2, T3, T4) -> R
): Flow<R> = combine(
    combine(flow, flow2, ::Pair),
    combine(flow3, flow4, ::Pair)
) { t1, t2 ->
    transform(
        t1.first,
        t1.second,
        t2.first,
        t2.second,
    )
}