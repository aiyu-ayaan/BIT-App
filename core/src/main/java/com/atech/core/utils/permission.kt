package com.atech.core.utils

import android.Manifest
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi

enum class Permissions(val value: String) {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    NOTIFICATION(Manifest.permission.POST_NOTIFICATIONS),
    WRITE_CALENDER(Manifest.permission.WRITE_CALENDAR),
    READ_CALENDER(Manifest.permission.READ_CALENDAR),
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU, lambda = 0)
inline fun <T> isAPI33AndUp(onSdk33: () -> T): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        onSdk33()
    } else null
}

//check for android 12
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S, lambda = 0)
inline fun <T> isAPI31AndUp(onSdk31: () -> T): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        onSdk31()
    } else null
}