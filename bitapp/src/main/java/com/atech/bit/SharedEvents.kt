package com.atech.bit

sealed class SharedEvents {
    data object ToggleSearchActive : SharedEvents()
}