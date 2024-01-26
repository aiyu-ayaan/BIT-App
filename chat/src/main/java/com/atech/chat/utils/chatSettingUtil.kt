/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.chat.utils

import android.content.SharedPreferences
import com.atech.chat.ChatSettingUiState
import com.atech.core.utils.SharePrefKeys
import com.atech.core.utils.fromJSON
import com.atech.core.utils.toJSON


fun getChatSetting(
    pref: SharedPreferences
) =
    fromJSON(
        pref.getString(SharePrefKeys.ChatScreenSetting.name, "")!!,
        ChatSettingUiState::class.java
    ) ?: ChatSettingUiState()

fun saveChatSetting(
    pref: SharedPreferences,
    chatSettingUiState: ChatSettingUiState
) {
    pref.edit().putString(
        SharePrefKeys.ChatScreenSetting.name,
        toJSON(chatSettingUiState)
    ).apply()
}
