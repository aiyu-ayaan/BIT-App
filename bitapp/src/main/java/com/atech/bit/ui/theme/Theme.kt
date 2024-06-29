/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import com.atech.bit.utils.animateColor

val lightColorScheme = ColorScheme(
    background = Color(0xfff9f9f9),
    error = Color(0xffba1a1a),
    errorContainer = Color(0xffffdad6),
    inverseOnSurface = Color(0xfff1f1f1),
    inversePrimary = Color(0xff60dbb9),
    inverseSurface = Color(0xff303030),
    onBackground = Color(0xff1b1b1b),
    onError = Color(0xffffffff),
    onErrorContainer = Color(0xff410002),
    onPrimary = Color(0xffffffff),
    onPrimaryContainer = Color(0xff002018),
    onSecondary = Color(0xffffffff),
    onSecondaryContainer = Color(0xff072019),
    onSurface = Color(0xff1b1b1b),
    onSurfaceVariant = Color(0xff474747),
    onTertiary = Color(0xffffffff),
    onTertiaryContainer = Color(0xff001e2c),
    outline = Color(0xff777777),
    outlineVariant = Color(0xffb4c2bf),
    primary = Color(0xff006b56),
    primaryContainer = Color(0xff7ef8d4),
    scrim = Color(0xff000000),
    secondary = Color(0xff4b635b),
    secondaryContainer = Color(0xffcee9dd),
    surface = Color(0xfff9f9f9),
    surfaceTint = Color(0xff006b56),
    surfaceVariant = Color(0xffe2e2e2),
    tertiary = Color(0xff416276),
    tertiaryContainer = Color(0xffc4e7ff),
    surfaceBright = Color(0xfff9f9f9),
    surfaceDim = Color(0xffdadada),
    surfaceContainer = Color(0xffeeeeee),
    surfaceContainerHigh = Color(0xffe8e8e8),
    surfaceContainerHighest = Color(0xffe2e2e2),
    surfaceContainerLow = Color(0xfff3f3f3),
    surfaceContainerLowest = Color(0xffffffff),
)

val darkColorScheme = ColorScheme(
    background = Color(0xff131313),
    error = Color(0xffffb4ab),
    errorContainer = Color(0xff93000a),
    inverseOnSurface = Color(0xff303030),
    inversePrimary = Color(0xff006b56),
    inverseSurface = Color(0xffe2e2e2),
    onBackground = Color(0xffe2e2e2),
    onError = Color(0xff690005),
    onErrorContainer = Color(0xffffdad6),
    onPrimary = Color(0xff00382b),
    onPrimaryContainer = Color(0xff7ef8d4),
    onSecondary = Color(0xff1d352d),
    onSecondaryContainer = Color(0xffcee9dd),
    onSurface = Color(0xffe2e2e2),
    onSurfaceVariant = Color(0xffc6c6c6),
    onTertiary = Color(0xff0d3446),
    onTertiaryContainer = Color(0xffc4e7ff),
    outline = Color(0xff919191),
    outlineVariant = Color(0xff3f4f4a),
    primary = Color(0xff60dbb9),
    primaryContainer = Color(0xff005140),
    scrim = Color(0xff000000),
    secondary = Color(0xffb2ccc2),
    secondaryContainer = Color(0xff344c43),
    surface = Color(0xff131313),
    surfaceTint = Color(0xff60dbb9),
    surfaceVariant = Color(0xff474747),
    tertiary = Color(0xffa8cbe2),
    tertiaryContainer = Color(0xff284b5e),
    surfaceBright = Color(0xff393939),
    surfaceDim = Color(0xff131313),
    surfaceContainer = Color(0xff1f1f1f),
    surfaceContainerHigh = Color(0xff2a2a2a),
    surfaceContainerHighest = Color(0xff353535),
    surfaceContainerLow = Color(0xff1b1b1b),
    surfaceContainerLowest = Color(0xff0e0e0e),
)


//@Composable
//fun isDark(isDark: Boolean): ColorScheme {
//    val background by
//    animateColor(isDark, lightColorScheme.background, darkColorScheme.background, "background")
//    val error by animateColor(isDark, lightColorScheme.error, darkColorScheme.error, "error")
//    val errorContainer by
//    animateColor(
//        isDark,
//        lightColorScheme.errorContainer,
//        darkColorScheme.errorContainer,
//        "errorContainer"
//    )
//    val inverseOnSurface by
//    animateColor(
//        isDark,
//        lightColorScheme.inverseOnSurface,
//        darkColorScheme.inverseOnSurface,
//        "inverseOnSurface"
//    )
//    val inversePrimary by
//    animateColor(
//        isDark,
//        lightColorScheme.inversePrimary,
//        darkColorScheme.inversePrimary,
//        "inversePrimary"
//    )
//    val inverseSurface by
//    animateColor(
//        isDark,
//        lightColorScheme.inverseSurface,
//        darkColorScheme.inverseSurface,
//        "inverseSurface"
//    )
//    val onBackground by
//    animateColor(
//        isDark,
//        lightColorScheme.onBackground,
//        darkColorScheme.onBackground,
//        "onBackground"
//    )
//    val onError by animateColor(
//        isDark,
//        lightColorScheme.onError,
//        darkColorScheme.onError,
//        "onError"
//    )
//    val onErrorContainer by
//    animateColor(
//        isDark,
//        lightColorScheme.onErrorContainer,
//        darkColorScheme.onErrorContainer,
//        "onErrorContainer"
//    )
//    val onPrimary by
//    animateColor(isDark, lightColorScheme.onPrimary, darkColorScheme.onPrimary, "onPrimary")
//    val onPrimaryContainer by
//    animateColor(
//        isDark,
//        lightColorScheme.onPrimaryContainer,
//        darkColorScheme.onPrimaryContainer,
//        "onPrimaryContainer"
//    )
//    val onSecondary by
//    animateColor(
//        isDark,
//        lightColorScheme.onSecondary,
//        darkColorScheme.onSecondary,
//        "onSecondary"
//    )
//    val onSecondaryContainer by
//    animateColor(
//        isDark,
//        lightColorScheme.onSecondaryContainer,
//        darkColorScheme.onSecondaryContainer,
//        "onSecondaryContainer"
//    )
//    val onSurface by
//    animateColor(isDark, lightColorScheme.onSurface, darkColorScheme.onSurface, "onSurface")
//    val onSurfaceVariant by
//    animateColor(
//        isDark,
//        lightColorScheme.onSurfaceVariant,
//        darkColorScheme.onSurfaceVariant,
//        "onSurfaceVariant"
//    )
//    val onTertiary by
//    animateColor(isDark, lightColorScheme.onTertiary, darkColorScheme.onTertiary, "onTertiary")
//    val onTertiaryContainer by
//    animateColor(
//        isDark,
//        lightColorScheme.onTertiaryContainer,
//        darkColorScheme.onTertiaryContainer,
//        "onTertiaryContainer"
//    )
//    val outline by animateColor(
//        isDark,
//        lightColorScheme.outline,
//        darkColorScheme.outline,
//        "outline"
//    )
//    val outlineVariant by
//    animateColor(
//        isDark,
//        lightColorScheme.outlineVariant,
//        darkColorScheme.outlineVariant,
//        "outlineVariant"
//    )
//    val primary by animateColor(
//        isDark,
//        lightColorScheme.primary,
//        darkColorScheme.primary,
//        "primary"
//    )
//    val primaryContainer by
//    animateColor(
//        isDark,
//        lightColorScheme.primaryContainer,
//        darkColorScheme.primaryContainer,
//        "primaryContainer"
//    )
//    val scrim by animateColor(isDark, lightColorScheme.scrim, darkColorScheme.scrim, "scrim")
//    val secondary by
//    animateColor(isDark, lightColorScheme.secondary, darkColorScheme.secondary, "secondary")
//    val secondaryContainer by
//    animateColor(
//        isDark,
//        lightColorScheme.secondaryContainer,
//        darkColorScheme.secondaryContainer,
//        "secondaryContainer"
//    )
//    val surface by animateColor(
//        isDark,
//        lightColorScheme.surface,
//        darkColorScheme.surface,
//        "surface"
//    )
//    val surfaceTint by animateColor(
//        isDark,
//        lightColorScheme.surfaceTint,
//        darkColorScheme.surfaceTint,
//        "surfaceTint"
//    )
//    val surfaceVariant by
//    animateColor(
//        isDark,
//        lightColorScheme.surfaceVariant,
//        darkColorScheme.surfaceVariant,
//        "surfaceVariant"
//    )
//    val tertiary by
//    animateColor(isDark, lightColorScheme.tertiary, darkColorScheme.tertiary, "tertiary")
//    val tertiaryContainer by
//    animateColor(
//        isDark,
//        lightColorScheme.tertiaryContainer,
//        darkColorScheme.tertiaryContainer,
//        "tertiaryContainer"
//    )
//    val surfaceBright by
//    animateColor(isDark, lightColorScheme.surface, darkColorScheme.surface, "surface")
//    val surfaceDim by
//    animateColor(isDark, lightColorScheme.surfaceDim, darkColorScheme.surfaceDim, "surfaceDim")
//    val surfaceContainer by
//    animateColor(
//        isDark,
//        lightColorScheme.surfaceContainer,
//        darkColorScheme.surfaceContainer,
//        "surfaceContainer"
//    )
//    val surfaceContainerHigh by
//    animateColor(
//        isDark,
//        lightColorScheme.surfaceContainerHigh,
//        darkColorScheme.surfaceContainerHigh,
//        "surfaceContainerHigh"
//    )
//    val surfaceContainerHighest by
//    animateColor(
//        isDark,
//        lightColorScheme.surfaceContainerHighest,
//        darkColorScheme.surfaceContainerHighest,
//        "surfaceContainerHighest"
//    )
//    val surfaceContainerLow by
//    animateColor(
//        isDark,
//        lightColorScheme.surfaceContainerLow,
//        darkColorScheme.surfaceContainerLow,
//        "surfaceContainerLow"
//    )
//    val surfaceContainerLowest by
//    animateColor(
//        isDark,
//        lightColorScheme.surfaceContainerLowest,
//        darkColorScheme.surfaceContainerLowest,
//        "surfaceContainerLowest"
//    )
//    return ColorScheme(
//        background = background,
//        error = error,
//        errorContainer = errorContainer,
//        inverseOnSurface = inverseOnSurface,
//        inversePrimary = inversePrimary,
//        inverseSurface = inverseSurface,
//        onBackground = onBackground,
//        onError = onError,
//        onErrorContainer = onErrorContainer,
//        onPrimary = onPrimary,
//        onPrimaryContainer = onPrimaryContainer,
//        onSecondary = onSecondary,
//        onSecondaryContainer = onSecondaryContainer,
//        onSurface = onSurface,
//        onSurfaceVariant = onSurfaceVariant,
//        onTertiary = onTertiary,
//        onTertiaryContainer = onTertiaryContainer,
//        outline = outline,
//        outlineVariant = outlineVariant,
//        primary = primary,
//        primaryContainer = primaryContainer,
//        scrim = scrim,
//        secondary = secondary,
//        secondaryContainer = secondaryContainer,
//        surface = surface,
//        surfaceTint = surfaceTint,
//        surfaceVariant = surfaceVariant,
//        tertiary = tertiary,
//        tertiaryContainer = tertiaryContainer,
//        surfaceBright = surfaceBright,
//        surfaceDim = surfaceDim,
//        surfaceContainer = surfaceContainer,
//        surfaceContainerHigh = surfaceContainerHigh,
//        surfaceContainerHighest = surfaceContainerHighest,
//        surfaceContainerLow = surfaceContainerLow,
//        surfaceContainerLowest = surfaceContainerLowest
//    )
//}


val ColorScheme.borderColor: Color
    @Composable
    get() = primary.copy(alpha = 0.6f)


val ColorScheme.captionColor: Color
    @Composable
    get() = onSurface.copy(alpha = 0.6f)
val ColorScheme.dividerOrCardColor: Color
    @Composable
    get() = primary.copy(alpha = .3f)


val ColorScheme.drawerColor: Color
    @Composable
    get() = ColorUtils.blendARGB(
        MaterialTheme.colorScheme.surface.toArgb(),
        MaterialTheme.colorScheme.primary.toArgb(),
        .09f
    ).let { Color(it) }


val grid_0_5 = 4.dp
val grid_1 = 8.dp
val grid_2 = 16.dp
val grid_3 = 24.dp
val image_view_log_in_size = 300.dp

val bottomPaddingSize = 100.dp
val image_size_for_card_view = 25.dp
val image_size_for_society_img = 80.dp
val holiday_data_card_view_size_home = 60.dp
val holiday_data_card_view_size = 80.dp
val min_padding_attendance = 100.dp
val div_height = 1.dp
val bottom_nav_height = 60.dp
val image_view_about_us_size = 100.dp
val course_image_size = 90.dp
val image_view_thumbnail = 230.dp
val card_view_attendance = 110.dp
val recycler_view_height_attendance = 120.dp
val empty_view_height = 300.dp
val user_profile_image_size = 30.dp
val video_view_height = 500.dp

@Composable
fun BITAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    statusBarColor: Color? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkColorScheme

        else -> lightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor =
                statusBarColor?.toArgb() ?: colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                !darkTheme && statusBarColor == null
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}