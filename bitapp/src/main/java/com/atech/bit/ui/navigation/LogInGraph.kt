package com.atech.bit.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.atech.bit.ui.screens.login.screen.login.LoginScreen
import com.atech.bit.utils.animatedComposable

sealed class LogInRoutes(val route: String) {
    data object LogIn : LogInRoutes("log_in")
}


fun NavGraphBuilder.logInScreenGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = LogInRoutes.LogIn.route,
        route = TopLevelRoute.LOGIN.route
    ) {
        animatedComposable(
            route = LogInRoutes.LogIn.route
        ) {
            LoginScreen(
                navController = navController
            )
        }
    }
}