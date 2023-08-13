package com.atech.theme

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner


inline fun Fragment.customBackPress(
    isEnable: Boolean = true,
    crossinline callback: () -> Unit
) = this.apply {
    viewLifecycleOwnerLiveData.observe(viewLifecycleOwner) { viewLifecycleOwner ->
        viewLifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    viewLifecycleOwner.lifecycle.removeObserver(this)
                    return
                }
                if (event == Lifecycle.Event.ON_CREATE) {
                    requireActivity().onBackPressedDispatcher.addCallback(this@customBackPress,
                        object : OnBackPressedCallback(isEnable) {
                            override fun handleOnBackPressed() {
                                callback.invoke()
                            }
                        })
                }
            }
        })
    }
}
