package com.atech.bit.utils

import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment

fun Fragment.handleCustomBackPressed(
    onBackPressed: OnBackPressedCallback.() -> Unit
) {
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
        onBackPressed()
    }
}
