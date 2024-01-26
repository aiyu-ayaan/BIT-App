/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import java.text.SimpleDateFormat
import java.util.Date


fun String.encodeUrlSpaces(): String = this.replace(" ", "%20")


fun Int.toBoolean() = this == 1


fun Number.ifZero(action: () -> Int): Number = if (this == 0) action() else this


fun Context.openAppSettings() = this.apply {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri = Uri.fromParts("package", this.packageName, null)
    intent.data = uri
    this.startActivity(intent)
}