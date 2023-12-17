package com.atech.bit.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.atech.bit.ui.activity.MainViewModel
import com.atech.bit.ui.screens.home.compose.HomeScreen
import com.atech.bit.utils.fadeThroughComposable


sealed class HomeScreenRoutes(val route: String) {
    data object HomeScreen : HomeScreenRoutes("home_screen")
}

fun NavGraphBuilder.homeGraph(
    navController: NavController,
    communicatorViewModel: MainViewModel
) {
    navigation(
        startDestination = HomeScreenRoutes.HomeScreen.route,
        route = RouteName.HOME.value
    ) {
        fadeThroughComposable(
            route = HomeScreenRoutes.HomeScreen.route
        ) {
            HomeScreen(
                communicatorViewModel = communicatorViewModel
            )
        }
    }

}
