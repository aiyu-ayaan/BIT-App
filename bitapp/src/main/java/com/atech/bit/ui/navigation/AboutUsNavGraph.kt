/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.atech.bit.ui.screens.about_us.AboutUsViewModel
import com.atech.bit.ui.screens.about_us.components.AboutUsScreen
import com.atech.bit.ui.screens.about_us.screen.credit.CreditsScreen
import com.atech.bit.ui.screens.about_us.screen.dev.DevDetailsScreen
import com.atech.bit.utils.animatedComposable
import com.atech.bit.utils.sharedViewModel


sealed class AboutUsRoute(val route: String) {
    data object AboutUsScreen : AboutUsRoute("aboutUs")

    data object DevDetailsScreen : AboutUsRoute("dev_details")

    data object CreditScreen : AboutUsRoute("credit_screen")
}

fun NavGraphBuilder.aboutUsNavGraph(
    navHostController: NavHostController,
) {
    navigation(
        startDestination = AboutUsRoute.AboutUsScreen.route, route = RouteName.ABOUT_US.value
    ) {
        animatedComposable(
            route = AboutUsRoute.AboutUsScreen.route
        ) {
            val viewModel = it.sharedViewModel<AboutUsViewModel>(navController = navHostController)
            AboutUsScreen(
                navController = navHostController, viewModel = viewModel
            )
        }
        animatedComposable(
            route = AboutUsRoute.DevDetailsScreen.route
        ) {
            val viewModel = it.sharedViewModel<AboutUsViewModel>(navController = navHostController)
            DevDetailsScreen(
                navController = navHostController,
                viewModel = viewModel
            )
        }
        animatedComposable(
            route = AboutUsRoute.CreditScreen.route
        ) {
            CreditsScreen(
                navController = navHostController,
            )
        }
    }
}