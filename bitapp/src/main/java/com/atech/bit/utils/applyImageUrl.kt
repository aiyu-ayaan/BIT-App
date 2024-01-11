/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.utils

import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

fun String.applyImageUrl(builder: NotificationCompat.Builder) = runBlocking(Dispatchers.IO) {
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
            NotificationCompat.BigPictureStyle().bigPicture(bitmap)
        )
    }
}