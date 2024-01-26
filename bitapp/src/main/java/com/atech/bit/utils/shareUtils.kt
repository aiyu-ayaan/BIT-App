/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.atech.bit.R
import com.atech.core.datasource.retrofit.BitAppApiService.Companion.BASE_URL
import com.atech.core.utils.encodeUrlSpaces
import com.atech.core.utils.handler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

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

fun Context.openShareString(message: String) =
    this.startActivity(Intent.createChooser(Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            message
        )
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, resources.getString(R.string.share_app))

        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }, null))

fun Context.shareOnlineSyllabus(pair: Pair<String, String>) = this.apply {
    Toast.makeText(this, "Getting Web Link", Toast.LENGTH_SHORT).show()
    val url = URL(
        "${BASE_URL}syllabus/${
            pair.second.replace(
                "\\d".toRegex(),
                ""
            )
        }/${pair.second}/subjects/${pair.first}".encodeUrlSpaces()
    )
    val text = """
            ${pair.first} .
            Link: $url
            
            Sauce: ${resources.getString(R.string.play_store_link)}$packageName
        """.trimIndent()
    openShareString(text)
}