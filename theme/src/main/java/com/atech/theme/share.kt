package com.atech.theme

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 *Extension function to open Share
 * @author Ayaan
 * @since 4.0.3
 */
enum class ShareType {
    NOTICE, EVENT, SYLLABUS
}

fun Activity.openShareDeepLink(
    title: String, path: String, shareType: ShareType = ShareType.NOTICE
) = this.startActivity(Intent.createChooser(Intent().apply {
    action = Intent.ACTION_SEND

    putExtra(
        Intent.EXTRA_TEXT, """
            $title .
            Link: ${
            Uri.parse(
                resources.getString(
                    when (shareType) {
                        ShareType.SYLLABUS -> R.string.deep_link_share_syllabus
                        ShareType.EVENT -> R.string.deep_link_share_event_link
                        ShareType.NOTICE -> R.string.deep_link_share_notice
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
    shareType: ShareType = ShareType.NOTICE
) = this.startActivity(Intent.createChooser(Intent().apply {
    action = Intent.ACTION_SEND
    putExtra(
        Intent.EXTRA_STREAM, saveFileToCaches(
            context, getBitMapUsingGlide(context, imageLink) ?: (ResourcesCompat.getDrawable(
                context.resources, R.drawable.poster, null
            ) as BitmapDrawable).toBitmap()
        )
    )
    putExtra(
        Intent.EXTRA_TEXT, """
            $title .
            Link: ${
            Uri.parse(
                when (shareType) {
                    ShareType.EVENT -> resources.getString(
                        R.string.deep_link_share_event_link, path.trim()
                    )

                    else -> resources.getString(
                        R.string.deep_link_share_notice,
                        path.trim()
                    )
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


fun getBitMapUsingGlide(context: Context, url: String): Bitmap? =
    runBlocking(Dispatchers.IO + handler) {
        try {
            val requestOptions =
                RequestOptions().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)

            Glide.with(context).asBitmap().load(url).apply(requestOptions).submit().get()
        } catch (e: GlideException) {
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
                context, "com.atech.bit" + ".provider", newFile
            )
        } catch (e: IOException) {
            null
        }
    }

/**
 *Extension function to share link
 * @author Ayaan
 * @since 4.0.3
 */
fun Activity.openShareLink() = this.startActivity(Intent.createChooser(Intent().apply {
    action = Intent.ACTION_SEND
    putExtra(
        Intent.EXTRA_STREAM, saveFileToCaches(
            this@openShareLink,
            (ResourcesCompat.getDrawable(
                this@openShareLink.resources, R.drawable.poster, null
            ) as BitmapDrawable).toBitmap()
        )
    )
    putExtra(
        Intent.EXTRA_TEXT,
        "${resources.getString(R.string.app_share_content)} \n" + "${resources.getString(R.string.play_store_link)}$packageName"
    )
    type = "image/*"
    putExtra(Intent.EXTRA_TITLE, resources.getString(R.string.share_app))

    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
}, null))
