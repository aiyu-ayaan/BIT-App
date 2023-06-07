package com.atech.bit.utils

import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

fun Fragment.handleCustomBackPressed(
    onBackPressed: OnBackPressedCallback.() -> Unit
) {
    viewLifecycleOwnerLiveData.observe(viewLifecycleOwner) { viewLifecycleOwner ->
        viewLifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    viewLifecycleOwner.lifecycle.removeObserver(this)
                    return
                }
                if (event == Lifecycle.Event.ON_CREATE)
                    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                        onBackPressed()
                    }

            }
        })
    }

}
