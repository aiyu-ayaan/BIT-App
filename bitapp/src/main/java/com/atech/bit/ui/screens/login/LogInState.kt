package com.atech.bit.ui.screens.login

data class LogInState(
    val isSignInSuccessful: Boolean = false,
    val userToken: String? = null,
    val isSignInError: String? = null
)