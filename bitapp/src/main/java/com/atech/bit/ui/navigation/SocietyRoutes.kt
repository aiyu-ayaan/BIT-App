package com.atech.bit.ui.navigation

import androidx.annotation.Keep
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.atech.bit.ui.screens.society.SocietyViewModel
import com.atech.bit.ui.screens.society.components.detail.SocietyDetailScreen
import com.atech.bit.ui.screens.society.components.main.SocietyScreen
import com.atech.bit.utils.animatedComposable
import com.atech.bit.utils.sharedViewModel


@Keep
sealed class SocietyRoutes(val routes: String) {
    data object SocietyScreen : SocietyRoutes("society_screen")
    data object DetailSocietyScreen : SocietyRoutes("detail_society_screen")
}

fun NavGraphBuilder.societyGraph(
    navHostController: NavHostController,
) {
    navigation(
        startDestination = SocietyRoutes.SocietyScreen.routes,
        route = RouteName.Society.value
    ) {
        animatedComposable(
            route = SocietyRoutes.SocietyScreen.routes
        ) {
            val viewModel = it.sharedViewModel<SocietyViewModel>(navHostController)
            SocietyScreen(
                navController = navHostController,
                viewModel = viewModel
            )
        }
        animatedComposable(
            route = SocietyRoutes.DetailSocietyScreen.routes
        ) {
            val viewModel = it.sharedViewModel<SocietyViewModel>(navHostController)
            SocietyDetailScreen(
                viewModel = viewModel,
                navController = navHostController
            )
        }
    }
}