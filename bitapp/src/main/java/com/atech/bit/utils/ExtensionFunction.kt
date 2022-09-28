package com.atech.bit.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.atech.bit.BuildConfig
import com.atech.core.R
import com.atech.core.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.color.MaterialColors
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


fun getUid(firebaseAuth: FirebaseAuth) = firebaseAuth.currentUser?.uid


/**
 * Extension function to open Compose Activity
 * @author Ayaan
 * @since 4.0.3
 */
fun Activity.openBugLink(
    @StringRes reportType: Int = R.string.bug_repost,
    extraString: String = "",
    reportDes: String? = null
) = this.startActivity(
    Intent.createChooser(
        Intent().also {
            it.putExtra(Intent.EXTRA_EMAIL, resources.getStringArray(R.array.email))
            it.putExtra(Intent.EXTRA_SUBJECT, resources.getString(reportType))
            it.putExtra(
                Intent.EXTRA_TEXT,
                if (extraString.isNotBlank()) """Found a bug on $extraString  
                                |happened on ${Date().time.convertLongToTime("dd/mm/yyyy hh:mm:aa")}
                                |due to $reportDes .
                                |
                                |Device Info: ${Build.MANUFACTURER} ${Build.MODEL}
                                |Android Version: ${Build.VERSION.RELEASE}
                                |App Version: ${BuildConfig.VERSION_NAME}
                                |""".trimMargin()
                else ""
            )
            it.type = "text/html"
            it.setPackage(resources.getString(R.string.gmail_package))
        }, resources.getString(R.string.bug_title)
    )
)


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

/**
 *
 *Extension function to calculate the time difference between two dates
 * @author Ayaan
 * @since 1.2.2
 */
fun Long.calculateTimeDifference(): String {
    val currentTime = System.currentTimeMillis()
    val difference = currentTime - this
    val seconds = difference / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    val months = days / 30
    val years = months / 12
    return when {
        seconds < 60 -> {
            "Just now"
        }
        minutes < 60 -> {
            "$minutes minutes ago"
        }
        hours < 24 -> {
            "$hours hours ago"
        }
        days < 30 -> {
            "$days days ago"
        }
        months < 12 -> {
            "$months months ago"
        }
        else -> {
            "$years years ago"
        }
    }
}

/**
 *Extension function to share link
 * @author Ayaan
 * @since 4.0.3
 */
fun Activity.openShareLink(link: String) =
    this.startActivity(Intent.createChooser(Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_STREAM,
            saveFileToCaches(
                this@openShareLink,
                try {
                    getBitMapUsingGlide(this@openShareLink, link)!!
                } catch (e: Exception) {
                    (ResourcesCompat.getDrawable(
                        this@openShareLink.resources,
                        R.drawable.app_logo,
                        null
                    ) as BitmapDrawable).toBitmap()
                }
            )
        )
        putExtra(
            Intent.EXTRA_TEXT, "${resources.getString(R.string.app_share_content)} \n" +
                    "${resources.getString(R.string.play_store_link)}$packageName"
        )
        type = "image/*"
        putExtra(Intent.EXTRA_TITLE, resources.getString(R.string.share_app))

        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }, null))
//
/**
 *Extension function to open Share
 * @author Ayaan
 * @since 4.0.3
 */
fun Activity.openShareDeepLink(
    title: String,
    path: String,
    share_type: String = SHARE_TYPE_NOTICE
) =
    this.startActivity(Intent.createChooser(Intent().apply {
        action = Intent.ACTION_SEND

        putExtra(
            Intent.EXTRA_TEXT, """
            $title .
            Link: ${
                Uri.parse(
                    resources.getString(
                        when (share_type) {
                            SHARE_TYPE_SYLLABUS -> R.string.deep_link_share_syllabus
                            SHARE_TYPE_EVENT -> R.string.deep_link_share_event_link
                            else -> R.string.deep_link_share_notice
                        }, path.trim()
                    )
                )
            }

            Sauce: ${resources.getString(R.string.play_store_link)}$packageName
        """.trimIndent()
        )
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, title)

        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }, null))


fun getBitMapUsingGlide(context: Context, url: String): Bitmap? =
    runBlocking(Dispatchers.IO + handler) {
        try {
            val requestOptions = RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)

            Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(requestOptions)
                .submit()
                .get()
        } catch (e: com.bumptech.glide.load.engine.GlideException) {
            throw e;
        }
    }


fun saveFileToCaches(context: Context, bitmap: Bitmap): Uri? =
    runBlocking(Dispatchers.IO + handler) {
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs() // don't forget to make the directory

            val stream =
                FileOutputStream("$cachePath/image.jpeg") // overwrites this image every time

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.close()

            val imagePath = File(context.cacheDir, "images")
            val newFile = File(imagePath, "image.jpeg")
            return@runBlocking FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".provider",
                newFile
            )
        } catch (e: IOException) {
            null
        }
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
    share_type: String = SHARE_EVENT
) =
    this.startActivity(Intent.createChooser(Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_STREAM,
            saveFileToCaches(
                context,
                getBitMapUsingGlide(context, imageLink) ?: (ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.app_logo,
                    null
                ) as BitmapDrawable).toBitmap()
            )
        )
        putExtra(
            Intent.EXTRA_TEXT, """
            $title .
            Link: ${
                Uri.parse(
                    when (share_type) {
                        SHARE_EVENT -> resources.getString(
                            R.string.deep_link_share_event_link,
                            path.trim()
                        )
                        else -> resources.getString(R.string.deep_link_share_notice, path.trim())
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


fun Activity.showMenuPrompt(
    @IdRes targetId: Int,
    @StringRes title: Int,
    description: String,
    listener: (() -> Unit)? = null
) = this.apply {
    MaterialTapTargetPrompt.Builder(this)
        .setTarget(targetId)
        .setPrimaryText(resources.getString(title))
        .setSecondaryText(description)
        .setBackgroundColour(
            MaterialColors.getColor(
                this,
                androidx.appcompat.R.attr.colorPrimary,
                Color.CYAN
            )
        )
        .setPrimaryTextColour(
            ContextCompat.getColor(
                this,
                com.atech.bit.R.color.textColor_oppo
            )
        )
        .setSecondaryTextColour(
            ContextCompat.getColor(
                this,
                com.atech.bit.R.color.textColorSecondary_oppo
            )
        )
        .setPromptStateChangeListener { _, state ->
            if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_DISMISSED
                || state == MaterialTapTargetPrompt.STATE_BACK_BUTTON_PRESSED || state == MaterialTapTargetPrompt.STATE_FINISHED
            ) {
                listener?.invoke()
            }
        }
        .show()
}

fun Context.loadAdds(adsView : AdView) =this.apply {
    val adRequest = AdRequest.Builder().build()
    adsView.loadAd(adRequest)
}
