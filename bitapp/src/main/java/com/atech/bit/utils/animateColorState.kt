/*
 *  Created by Aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.utils

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun animateColor(isLight: Boolean = true, lightColor: Color, darkColor: Color, label: String) =
    animateColorAsState(
        if (isLight) lightColor else darkColor, label = label,
        animationSpec = tween(
            durationMillis = 2000,
            delayMillis = 40,
            easing = LinearOutSlowInEasing
        )
    )
