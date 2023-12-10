package com.atech.view_model

import androidx.compose.material3.DrawerValue

sealed class SharedEvents {
    data object ToggleSearchActive : SharedEvents()

    data class ToggleDrawer( val state : DrawerValue?) : SharedEvents()
}