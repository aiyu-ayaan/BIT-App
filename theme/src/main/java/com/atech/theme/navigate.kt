package com.atech.theme

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController

fun Fragment.navigate(directions: NavDirections, extra: Navigator.Extras? = null) = this.apply {
    try {
        if (extra == null)
            findNavController().navigate(directions)
        else
            findNavController().navigate(directions, extra)
    } catch (e: Exception) {
        toast("Press one item at a time")
    }
}