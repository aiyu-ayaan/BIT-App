/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.utils

import android.content.SharedPreferences
import com.atech.bit.ui.activity.main.ThemeState
import com.atech.core.utils.SharePrefKeys
import com.atech.core.utils.fromJSON
import com.atech.core.utils.toJSON


fun saveTheme(theme: ThemeState, pref: SharedPreferences) {
    val json = toJSON(theme)
    pref.edit().putString(SharePrefKeys.AppThemeState.name, json).apply()
}

fun getTheme(pref: SharedPreferences): ThemeState {
    val json = pref.getString(SharePrefKeys.AppThemeState.name, "")
    return fromJSON(json!!, ThemeState::class.java) ?: ThemeState()
}