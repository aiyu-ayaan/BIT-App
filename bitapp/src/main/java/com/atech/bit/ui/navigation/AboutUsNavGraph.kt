package com.atech.bit.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.atech.bit.ui.screens.about_us.AboutUsViewModel
import com.atech.bit.ui.screens.about_us.components.AboutUsScreen
import com.atech.bit.utils.animatedComposable
import com.atech.bit.utils.sharedViewModel


sealed class AboutUsRoute(val route: String) {
    data object AboutUsScreen : AboutUsRoute("aboutUs")
}

fun NavGraphBuilder.aboutUsNavGraph(
    navHostController: NavHostController,
) {
    navigation(
        startDestination = AboutUsRoute.AboutUsScreen.route,
        route = RouteName.ABOUT_US.value
    ) {
        animatedComposable(
            route = AboutUsRoute.AboutUsScreen.route
        ) {
            val viewModel = it.sharedViewModel<AboutUsViewModel>(navController = navHostController)
            AboutUsScreen(
                navController = navHostController,
                viewModel = viewModel
            )
        }
    }
}