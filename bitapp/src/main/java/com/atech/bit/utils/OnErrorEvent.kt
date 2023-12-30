package com.atech.bit.utils

sealed interface OnErrorEvent {
    data class OnError(val message: String) : OnErrorEvent
}