/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.login

sealed interface LogInScreenEvents {
    data class SaveCoursePref(val course: String, val sem: String) : LogInScreenEvents
    data class OnSignInResult(val state: LogInState) : LogInScreenEvents

    data object SetUID : LogInScreenEvents
}