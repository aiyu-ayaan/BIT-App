package com.atech.theme

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
    primaryContainer = GreenGray40
)

val ColorScheme.borderColor: Color
    @Composable
    get() = primary.copy(alpha = 0.6f)


val ColorScheme.captionColor: Color
    @Composable
    get() = onSurface.copy(alpha = 0.6f)

val ColorScheme.dividerColor: Color
    @Composable
    get() = primary.copy(alpha = .3f)


val grid_0_5 = 4.dp
val grid_1 = 8.dp
val grid_2 = 16.dp
val grid_3 = 24.dp

val bottomPaddingSize = 100.dp


private val LightColorScheme = lightColorScheme(
    primary = Green80,
    secondary = GreenGray80,
    tertiary = LightGreen80,
    surface = ColorSurfaceLight,
    background = ColorSurfaceLight,
    primaryContainer = GreenGray80
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