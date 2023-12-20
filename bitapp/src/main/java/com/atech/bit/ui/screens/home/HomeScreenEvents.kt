package com.atech.bit.ui.screens.home

sealed class HomeScreenEvents {
    data class ToggleOnlineSyllabusClick(val isOnline: Boolean) : HomeScreenEvents()

    data class OnCourseChange(val value: Pair<String, String>) : HomeScreenEvents()
}