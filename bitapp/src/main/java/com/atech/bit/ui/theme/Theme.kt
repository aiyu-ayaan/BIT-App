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
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat

val lightColorScheme = ColorScheme(
    background = Color(0xff020707),
    error = Color(0xff35d7db),
    errorContainer = Color(0xff003238),
    inverseOnSurface = Color(0xff4e5051),
    inversePrimary = Color(0xff737373),
    inverseSurface = Color(0xffcecfcf),
    onBackground = Color(0xff6d7070),
    onError = Color(0xff000000),
    onErrorContainer = Color(0xff61fff4),
    onPrimary = Color(0xff000000),
    onPrimaryContainer = Color(0xffb3b2b2),
    onSecondary = Color(0xff000000),
    onSecondaryContainer = Color(0xffb2b3b3),
    onSurface = Color(0xffb1b2b2),
    onSurfaceVariant = Color(0xff959697),
    onTertiary = Color(0xff000000),
    onTertiaryContainer = Color(0xffb2b3b2),
    outline = Color(0xff717374),
    outlineVariant = Color(0xff333636),
    primary = Color(0xff969696),
    primaryContainer = Color(0xff252727),
    scrim = Color(0xff000000),
    secondary = Color(0xff959696),
    secondaryContainer = Color(0xff242727),
    surface = Color(0xff020707),
    surfaceTint = Color(0xffa1a1a1),
    surfaceVariant = Color(0xff1a1d1e),
    tertiary = Color(0xff959696),
    tertiaryContainer = Color(0xff242727),
    surfaceBright = Color(0xff020707),
    surfaceDim = Color(0xff222627),
    surfaceContainer = Color(0xff0e1213),
    surfaceContainerHigh = Color(0xff141818),
    surfaceContainerHighest = Color(0xff1a1d1e),
    surfaceContainerLow = Color(0xff080c0d),
    surfaceContainerLowest = Color(0xff000000),
)

val darkColorScheme = ColorScheme(
    background = Color(0xffebecec),
    error = Color(0xff009eaa),
    errorContainer = Color(0xff84fff8),
    inverseOnSurface = Color(0xffabadad),
    inversePrimary = Color(0xff909090),
    inverseSurface = Color(0xff1a1d1e),
    onBackground = Color(0xff9c9d9e),
    onError = Color(0xffc3fffe),
    onErrorContainer = Color(0xff005b65),
    onPrimary = Color(0xffe7e6e6),
    onPrimaryContainer = Color(0xff424343),
    onSecondary = Color(0xffe6e6e6),
    onSecondaryContainer = Color(0xff414344),
    onSurface = Color(0xff424545),
    onSurfaceVariant = Color(0xff67696a),
    onTertiary = Color(0xffe6e6e6),
    onTertiaryContainer = Color(0xff414443),
    outline = Color(0xff8c8d8e),
    outlineVariant = Color(0xffbabbbb),
    primary = Color(0xff696969),
    primaryContainer = Color(0xffc6c5c5),
    scrim = Color(0xff000000),
    secondary = Color(0xff67696a),
    secondaryContainer = Color(0xffc5c5c5),
    surface = Color(0xffebecec),
    surfaceTint = Color(0xff383939),
    surfaceVariant = Color(0xffb8b8b9),
    tertiary = Color(0xff676969),
    tertiaryContainer = Color(0xffc4c5c5),
    surfaceBright = Color(0xffc5c6c6),
    surfaceDim = Color(0xffebecec),
    surfaceContainer = Color(0xffdfe0e0),
    surfaceContainerHigh = Color(0xffd4d5d5),
    surfaceContainerHighest = Color(0xffcacbcb),
    surfaceContainerLow = Color(0xffe3e4e4),
    surfaceContainerLowest = Color(0xfff1f1f1),
)

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