package com.atech.view_model

sealed class SharedEvents {
    data object ToggleSearchActive : SharedEvents()
}