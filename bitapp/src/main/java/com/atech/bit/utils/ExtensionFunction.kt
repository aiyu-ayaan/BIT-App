package com.atech.bit.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
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
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


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
                getBitMapUsingGlide(this@openShareLink, link) ?: (ResourcesCompat.getDrawable(
                    this@openShareLink.resources,
                    R.drawable.app_logo,
                    null
                ) as BitmapDrawable).toBitmap()
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
                || state == MaterialTapTargetPrompt.STATE_BACK_BUTTON_PRESSED ||  state == MaterialTapTargetPrompt.STATE_FINISHED) {
                listener?.invoke()
            }
        }
        .show()

}