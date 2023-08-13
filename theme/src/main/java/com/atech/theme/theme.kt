package com.atech.theme

import androidx.appcompat.app.AppCompatDelegate


enum class Theme(val value: Int) {
    LIGHT(AppCompatDelegate.MODE_NIGHT_NO),
    DARK(AppCompatDelegate.MODE_NIGHT_YES),
    SYSTEM(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
}

/**
 * Change Theme
 * @since 4.0.4
 * @author Ayaan
 */
fun setAppTheme(theme: Theme) {
    AppCompatDelegate.setDefaultNightMode(theme.value)
}

/**
 * Change Theme
 * @since 4.0.4
 * @author Ayaan
 */
fun setAppTheme(type: Int) {
    AppCompatDelegate.setDefaultNightMode(type)
}