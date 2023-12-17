package com.atech.bit.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.atech.bit.ui.screens.library.components.LibraryManagerScreen
import com.atech.bit.utils.animatedComposable


sealed class LibraryRoute(val route: String) {
    data object LibraryManagerScreen : LibraryRoute("library_manager_screen")
    data object LibraryDetailScreen : LibraryRoute("library_detail_screen")
}


fun NavGraphBuilder.libraryGraph(
    navHostController: NavHostController
) {
    navigation(
        startDestination = LibraryRoute.LibraryManagerScreen.route,
        route = RouteName.LIBRARY.value
    ) {
        animatedComposable(route = LibraryRoute.LibraryManagerScreen.route) {
            LibraryManagerScreen(
                navHostController = navHostController
            )
        }
    }

}
