package com.atech.bit.ui.screens.login

sealed class LogInScreenEvents {
    data class SaveCoursePref(val course: String, val sem: String) : LogInScreenEvents()
}