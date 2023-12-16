package com.atech.view_model

sealed class OnErrorEvent {
    data class OnError(val message: String) : OnErrorEvent()
}