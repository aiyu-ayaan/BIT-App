package com.atech.theme.base_class

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.atech.theme.Axis
import com.atech.theme.enterTransition

open class BaseFragment(
    @LayoutRes id: Int, private val axis: Axis
) : Fragment(id) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enterTransition(axis = axis)

        viewLifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    viewLifecycleOwner.lifecycle.removeObserver(this)
                    return
                }
                if (event == Lifecycle.Event.ON_CREATE) {
                    requireActivity().onBackPressedDispatcher.addCallback(
                        object : OnBackPressedCallback(true) {
                            override fun handleOnBackPressed() {
                                findNavController().popBackStack()
                            }
                        })
                }
            }
        })
    }
}
