package com.atech.bit.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.atech.bit.ui.activity.MainViewModel
import com.atech.bit.ui.screens.home.compose.HomeScreen
import com.atech.bit.ui.screens.home.screen.notice.NoticeViewModel
import com.atech.bit.ui.screens.home.screen.notice.detail.compose.NoticeDetailScreen
import com.atech.bit.ui.screens.home.screen.notice.notice.compose.NoticeScreen
import com.atech.bit.utils.animatedComposable
import com.atech.bit.utils.fadeThroughComposable
import com.atech.bit.utils.sharedViewModel


sealed class NoticeScreenRoute(val route: String) {
    data object NoticeScreen : NoticeScreenRoute("notice_screen")
    data object NoticeDetailsScreen : NoticeScreenRoute("notice_details_screen")
}

fun NavGraphBuilder.noticeGraph(
    navController: NavController,
) {
    navigation(
        startDestination = NoticeScreenRoute.NoticeScreen.route,
        route = HomeScreenRoutes.NoticeScreen.route
    ) {
        fadeThroughComposable(
            route = NoticeScreenRoute.NoticeScreen.route
        ) {
            val viewModel = it.sharedViewModel<NoticeViewModel>(navController = navController)
            NoticeScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        animatedComposable(
            route = NoticeScreenRoute.NoticeDetailsScreen.route
        ) {
            val viewModel = it.sharedViewModel<NoticeViewModel>(navController = navController)
            NoticeDetailScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}

sealed class HomeScreenRoutes(val route: String) {
    data object HomeScreen : HomeScreenRoutes("home_screen")

    data object NoticeScreen : HomeScreenRoutes("notice_route")
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
                communicatorViewModel = communicatorViewModel,
                navController = navController
            )
        }
        noticeGraph(navController)
    }

}
