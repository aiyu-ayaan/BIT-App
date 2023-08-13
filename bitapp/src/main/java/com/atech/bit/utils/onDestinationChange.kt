package com.atech.bit.utils

import androidx.navigation.NavController
import androidx.navigation.NavDestination

inline fun NavController.onDestinationChange(crossinline des: ((NavDestination) -> Unit)) =
    this.addOnDestinationChangedListener { _, destination, _ ->
        des.invoke(destination)
    }