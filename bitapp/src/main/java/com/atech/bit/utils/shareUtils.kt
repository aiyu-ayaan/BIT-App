package com.atech.bit.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.atech.bit.R
import com.atech.core.utils.handler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 *Extension function to share link
 * @author Ayaan
 * @since 4.0.3
 */
fun Context.openShareApp() = this.startActivity(Intent.createChooser(Intent().apply {
    action = Intent.ACTION_SEND
    putExtra(
        Intent.EXTRA_STREAM, saveFileToCaches(
            this@openShareApp,
            (ResourcesCompat.getDrawable(
                this@openShareApp.resources, R.drawable.poster, null
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
