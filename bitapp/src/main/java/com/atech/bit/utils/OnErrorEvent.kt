package com.atech.bit.utils

sealed class OnErrorEvent {
    data class OnError(val message: String) : OnErrorEvent()
}