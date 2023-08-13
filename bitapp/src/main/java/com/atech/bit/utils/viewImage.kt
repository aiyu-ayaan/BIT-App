package com.atech.bit.utils

import androidx.fragment.app.Fragment
import com.atech.bit.NavGraphDirections
import com.atech.theme.Axis
import com.atech.theme.exitTransition
import com.atech.theme.navigate


/***
 * This is a extension function for navigate to ViewImageFragment
 * @param data Pair<String,String> link and title
 */
fun Fragment.navigateToViewImage(data: Pair<String, String>) = this.apply {
    exitTransition(Axis.Z)
    navigate(
        NavGraphDirections.actionGlobalViewImageFragment(
            data.first,
            data.second
        )
    )
}