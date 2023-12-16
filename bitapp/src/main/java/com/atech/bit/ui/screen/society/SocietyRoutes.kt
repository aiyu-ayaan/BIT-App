package com.atech.bit.ui.screen.society

import androidx.annotation.Keep
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.atech.bit.ui.screen.society.components.detail.SocietyDetailScreen
import com.atech.bit.ui.screen.society.components.main.SocietyScreen
import com.atech.utils.animatedCompose
import com.atech.utils.getSimpleName
import com.atech.utils.sharedViewModel


@Keep
sealed class SocietyRoutes(val routes: String) {
    data object SocietyScreen : SocietyRoutes("society_screen")
    data object DetailSocietyScreen : SocietyRoutes("detail_society_screen")
}

fun NavGraphBuilder.societyNavigation(
    navHostController: NavHostController,
) {
    navigation(
        startDestination = SocietyRoutes.SocietyScreen.routes,
        route = getSimpleName(SocietyRoutes::class.java)
    ) {
        animatedCompose(
            route = SocietyRoutes.SocietyScreen.routes
        ) {
            val viewModel = it.sharedViewModel<SocietyViewModel>(navHostController)
            SocietyScreen(
                navController = navHostController,
                viewModel = viewModel
            )
        }
        animatedCompose(
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