/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.login

data class LogInState(
    val isNewUser: Boolean = false,
    val userToken: String? = null,
    val errorMessage: String? = null
)