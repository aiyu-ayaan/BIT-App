package com.atech.chat

sealed interface ChatScreenEvents {
    data class OnNewMessage(val message: String) : ChatScreenEvents

    data object OnCancelClick : ChatScreenEvents

    data class OnChatDelete(val model : ChatMessage) : ChatScreenEvents

    data object OnDeleteAllClick : ChatScreenEvents

}