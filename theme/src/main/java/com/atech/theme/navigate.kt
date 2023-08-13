package com.atech.theme

import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavDirections
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController

private const val TAG = "navigate"

fun Fragment.getLastDestination() = this.let {
    findNavController().previousBackStackEntry?.destination?.id
}


fun Fragment.navigateWithInAppDeepLink(url: String) = this.apply {
    val action = NavDeepLinkRequest.Builder
        .fromUri(url.toUri())
        .build()
    navigate(action)
}


fun Fragment.navigate(directions: NavDirections, extra: Navigator.Extras? = null) = this.apply {
    try {
        if (extra == null) findNavController().navigate(directions)
        else findNavController().navigate(directions, extra)
    } catch (e: Exception) {
        toast("Something went wrong")
        Log.d(TAG, "navigate: $e")
    }
}

fun Fragment.navigate(request: NavDeepLinkRequest) = this.apply {
    try {
        findNavController().navigate(request)
    } catch (e: Exception) {
        toast("Press one item at a time")
        Log.d(TAG, "navigate: $e")
    }
}

fun Fragment.navigate(@IdRes resId: Int, args: Bundle?) = this.apply {
    try {
        findNavController().navigate(
            resId, args
        )
    } catch (e: Exception) {
        toast("Press one item at a time")
        Log.d(TAG, "navigate: $e")
    }
}