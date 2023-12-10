package com.atech.view_model

import androidx.compose.material3.DrawerValue

fun toggleDrawer(communicatorViewModel: SharedViewModel) =
    if (communicatorViewModel.toggleDrawerState.value == DrawerValue.Closed)
        DrawerValue.Open else DrawerValue.Closed