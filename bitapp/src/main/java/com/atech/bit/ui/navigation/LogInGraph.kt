package com.atech.bit.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.atech.bit.ui.activity.main.MainViewModel
import com.atech.bit.ui.screens.login.LogInViewModel
import com.atech.bit.ui.screens.login.screen.login.LoginScreen
import com.atech.bit.ui.screens.login.screen.setup.SetUpScreen
import com.atech.bit.utils.animatedComposable
import com.atech.bit.utils.sharedViewModel

sealed class LogInRoutes(val route: String) {
    data object LogInScreen : LogInRoutes("log_in")
    data object SetupScreen : LogInRoutes("setup_screen")
}


fun NavGraphBuilder.logInScreenGraph(
    navController: NavHostController,
    communicatorViewModel: MainViewModel
) {
    navigation(
        startDestination = LogInRoutes.LogInScreen.route,
        route = TopLevelRoute.LOGIN.route
    ) {
        animatedComposable(
            route = LogInRoutes.LogInScreen.route
        ) {
            val viewModel = it.sharedViewModel<LogInViewModel>(navController = navController)
            LoginScreen(
                navController = navController,
                viewModel = viewModel,
                communicatorViewModel = communicatorViewModel
            )
        }
        animatedComposable(
            route = LogInRoutes.SetupScreen.route
        ) {
            val viewModel = it.sharedViewModel<LogInViewModel>(navController = navController)
            SetUpScreen(
                navController = navController,
                communicatorViewModel = communicatorViewModel,
                viewModel = viewModel
            )
        }
    }
}