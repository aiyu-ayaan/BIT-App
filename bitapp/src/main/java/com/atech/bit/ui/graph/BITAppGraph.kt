package com.atech.bit.ui.graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.atech.bit.ui.screen.MainScreen
import com.atech.utils.animatedCompose

sealed class BITAppRoutes(val route: String) {
    data object MainScreen : BITAppRoutes("main_screen")
}

@Composable
fun BITAppRootGraph(
    navHostController: NavHostController
) {
    NavHost(
        navController = navHostController, startDestination = BITAppRoutes.MainScreen.route
    ) {
        animatedCompose(
            route = BITAppRoutes.MainScreen.route
        ) {
            MainScreen()
        }
    }
}