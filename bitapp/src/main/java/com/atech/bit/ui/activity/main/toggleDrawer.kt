package com.atech.bit.ui.activity.main

import androidx.compose.material3.DrawerValue

fun toggleDrawer(communicatorViewModel: MainViewModel) =
    if (communicatorViewModel.toggleDrawerState.value == DrawerValue.Closed)
        DrawerValue.Open else DrawerValue.Closed