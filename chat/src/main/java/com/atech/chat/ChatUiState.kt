/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.chat

import androidx.compose.runtime.toMutableStateList

class ChatUiState(
    messages: List<ChatMessage> = emptyList()
) {
    private val _messages: MutableList<ChatMessage> = messages.toMutableStateList()
    val messages: List<ChatMessage> = _messages

    fun addMessage(msg: ChatMessage) {
        _messages.add(msg)
    }

    fun getLastMessage(): ChatMessage? {
        return _messages.lastOrNull()
    }
}

data class ChatSettingUiState(
    val isKeepChat: Boolean = true,
    val isAutoDeleteChat: Boolean = false,
    val isWrapWord: Boolean = true
)

sealed interface ChatSettingsEvent {
    data object KeepChat : ChatSettingsEvent
    data object AutoDeleteChat : ChatSettingsEvent
    data object WrapWord : ChatSettingsEvent
}