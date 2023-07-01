package com.atech.theme

import android.graphics.Color
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis

val FragmentManager.currentNavigationFragment: Fragment?
    get() = primaryNavigationFragment?.childFragmentManager?.fragments?.first()

enum class Axis(val value: Int) {
    X(MaterialSharedAxis.X),
    Y(MaterialSharedAxis.Y),
    Z(MaterialSharedAxis.Z)
}

fun Fragment.exitTransition(axis: Axis = Axis.Y) =
    this.run {
        exitTransition = MaterialSharedAxis(axis.value, true)
        reenterTransition = MaterialSharedAxis(axis.value, false)
    }

fun Fragment.enterTransition(axis: Axis = Axis.Y) =
    this.run {
        enterTransition = MaterialSharedAxis(axis.value, true)
        returnTransition = MaterialSharedAxis(axis.value, false)
    }

fun Fragment.enterSharedElementTransform(parent: Int) = this.apply {
    sharedElementEnterTransition = MaterialContainerTransform().apply {
        drawingViewId = parent
        duration = resources.getInteger(R.integer.duration_small).toLong()
        scrimColor = Color.TRANSPARENT
        setAllContainerColors(Color.TRANSPARENT)
    }
}

fun Fragment.exitSharedElementTransform() = this.apply {
    exitTransition = MaterialElevationScale(false).apply {
        duration = resources.getInteger(R.integer.duration_medium).toLong()
    }
    reenterTransition = MaterialElevationScale(true).apply {
        duration = resources.getInteger(R.integer.duration_small).toLong()
    }
}