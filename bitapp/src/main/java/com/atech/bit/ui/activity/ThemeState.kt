package com.atech.bit.ui.activity

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

data class ThemeState(
    val isDarkTheme: ThemeMode = ThemeMode.SYSTEM,
    val isDynamicColorActive: Boolean = true
)

sealed class ThemeEvent {
    data class ChangeTheme(
        val state: ThemeState
    ) : ThemeEvent()
}