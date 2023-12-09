package com.atech.utils

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable




const val duration = 300
const val outOffset = -300
const val inOffset = 300
fun NavGraphBuilder.animatedCompose(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) = this.apply {
    composable(
        route = route,
        content = content,
        arguments = arguments,
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { outOffset },
                animationSpec = tween(
                    durationMillis = duration,
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = duration
                )
            )
        },
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { inOffset },
                animationSpec = tween(
                    durationMillis = duration,
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = duration
                )
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { outOffset },
                animationSpec = tween(
                    durationMillis = duration,
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = duration
                )
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { inOffset },
                animationSpec = tween(
                    durationMillis = duration,
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = duration
                )
            )
        }
    )
}

