/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.home

sealed interface HomeScreenEvents {
    data class ToggleOnlineSyllabusClick(val isOnline: Boolean) : HomeScreenEvents

    data class OnCourseChange(val value: Pair<String, String>) : HomeScreenEvents
}