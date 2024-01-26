/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.activity.main

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

data class ThemeState(
    val isDarkTheme: ThemeMode = ThemeMode.SYSTEM,
    val isDynamicColorActive: Boolean = true
)

sealed interface ThemeEvent {
    data class ChangeTheme(
        val state: ThemeState
    ) : ThemeEvent
}