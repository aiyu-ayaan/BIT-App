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
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Green40,
    secondary = GreenGray40,
    tertiary = LightGreen40,
    surface = ColorSurfaceDark,
    background = ColorSurfaceDark,
    onPrimary = Color.White,
    primaryContainer = surfaceContainerDark,
    secondaryContainer = secondaryContainerDark,
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


private val LightColorScheme = lightColorScheme(
    primary = Green80,
    secondary = GreenGray80,
    tertiary = LightGreen80,
    surface = ColorSurfaceLight,
    background = ColorSurfaceLight,
    primaryContainer = surfaceContainerLight,
    secondaryContainer = secondaryContainerLight,
)


@Composable
fun BITAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme

        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}