package com.atech.theme

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.transition.MaterialSharedAxis

val FragmentManager.currentNavigationFragment: Fragment?
    get() = primaryNavigationFragment?.childFragmentManager?.fragments?.first()

fun Fragment.exitTransition(@MaterialSharedAxis.Axis axis: Int = MaterialSharedAxis.Y) =
    this.apply {
        exitTransition = MaterialSharedAxis(axis, true)
        reenterTransition = MaterialSharedAxis(axis, false)
    }

fun Fragment.enterTransition(@MaterialSharedAxis.Axis axis: Int = MaterialSharedAxis.Y) =
    this.apply {
        enterTransition = MaterialSharedAxis(axis, true)
        returnTransition = MaterialSharedAxis(axis, false)
    }