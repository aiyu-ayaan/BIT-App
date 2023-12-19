package com.atech.bit.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.atech.bit.ui.activity.MainViewModel
import com.atech.bit.ui.screens.settings.component.SettingScreen
import com.atech.bit.utils.animatedComposable


sealed class SettingRoutes(
    val route: String
) {
    data object SettingScreen : SettingRoutes("setting")

}

fun NavGraphBuilder.settingNavigationGraph(
    navHostController: NavController,
    mainViewModel: MainViewModel
) {
    navigation(
        startDestination = SettingRoutes.SettingScreen.route,
        route = RouteName.SETTINGS.value
    ) {
        animatedComposable(
            route = SettingRoutes.SettingScreen.route,
        ) {
            SettingScreen(
                navController = navHostController,
                viewModel = mainViewModel
            )
        }
    }
}