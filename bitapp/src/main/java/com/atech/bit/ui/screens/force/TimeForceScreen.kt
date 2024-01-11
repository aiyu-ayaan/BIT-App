/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.force

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.atech.bit.R
import com.atech.bit.ui.navigation.ParentScreenRoutes
import com.atech.bit.ui.theme.grid_0_5
import com.atech.bit.ui.theme.grid_1

@Composable
fun TimeForceScreen(
    modifier: Modifier = Modifier,
    state: Boolean,
    navController: NavHostController
) {
    LaunchedEffect(key1 = state) {
        if (state) {
            navController.popBackStack()
            navController.navigate(
                ParentScreenRoutes.MainScreen.route
            )
        }
    }
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(grid_1),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.time_error),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(.8f)
                .fillMaxHeight(.5f)
        )
        Spacer(modifier = Modifier.padding(grid_1))
        Text(
            text = "Your system time is incorrect. Please set your time to automatic.",
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(grid_1))
        TextButton(onClick = {
            context.openTimeSetting()
        }) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = null,
                modifier = Modifier.padding(grid_0_5)
            )
            Text(
                text = "Open Settings", modifier = Modifier.padding(grid_0_5)
            )
        }
    }
}

private fun Context.openTimeSetting() {
    startActivity(Intent(Settings.ACTION_DATE_SETTINGS))
}
