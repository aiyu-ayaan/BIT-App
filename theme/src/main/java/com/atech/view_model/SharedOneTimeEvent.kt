package com.atech.view_model

sealed class SharedOneTimeEvent {
    data class OnError(val message: String) : SharedOneTimeEvent()
}