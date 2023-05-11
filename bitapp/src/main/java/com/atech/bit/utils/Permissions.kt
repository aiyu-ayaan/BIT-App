package com.atech.bit.utils

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment

enum class Permissions(val value: String) {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    POST_NOTIFICATION(Manifest.permission.POST_NOTIFICATIONS),
}

inline fun <T> isAPI33AndUp(onSdk33: () -> T): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        onSdk33()
    } else null
}
inline fun <F : Fragment, T> checkPerm(
    fragment: F,
    permission: String,
    onGranted: () -> T,
): T? =
    fragment.run {
        if (requireContext().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED)
            return onGranted()
        else
            return null
    }

