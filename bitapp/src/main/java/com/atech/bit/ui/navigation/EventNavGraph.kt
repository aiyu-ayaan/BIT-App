package com.atech.bit.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.atech.bit.ui.screens.event.EventViewModel
import com.atech.bit.ui.screens.event.component.detail.EventDetailScreen
import com.atech.bit.ui.screens.event.component.event.EventScreen
import com.atech.bit.utils.animatedComposable
import com.atech.bit.utils.sharedViewModel


sealed class EventRoute(val route: String) {
    data object EventScreen : EventRoute("event_screen")
    data object DetailScreen : EventRoute("detail_screen")
}

fun NavGraphBuilder.eventGraph(
    navHostController: NavHostController,
) = this.apply {
    navigation(
        startDestination = EventRoute.EventScreen.route,
        route = RouteName.EVENT.value
    ) {
        animatedComposable(
            route = EventRoute.EventScreen.route
        ) {
            val viewModel = it.sharedViewModel<EventViewModel>(navController = navHostController)
            EventScreen(
                navController = navHostController,
                viewModel = viewModel
            )
        }
        animatedComposable(
            route = EventRoute.DetailScreen.route + "?eventId={eventId}",
            arguments = listOf(
                navArgument("eventId") {
                    type = NavType.LongType
                    defaultValue = -1L
                })
        ) {
            val viewModel = it.sharedViewModel<EventViewModel>(navController = navHostController)
            EventDetailScreen(
                navController = navHostController,
                viewModel = viewModel
            )
        }
    }
}