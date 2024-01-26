/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.chat

sealed interface ChatScreenEvents {
    data class OnNewMessage(val message: String) : ChatScreenEvents

    data object OnCancelClick : ChatScreenEvents

    data class OnChatDelete(val model : ChatMessage) : ChatScreenEvents

    data object OnDeleteAllClick : ChatScreenEvents

}