package com.atech.bit.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.atech.bit.ui.screens.library.LibraryManagerViewModel
import com.atech.bit.ui.screens.library.addedit.AddEditLibraryScreen
import com.atech.bit.ui.screens.library.components.LibraryManagerScreen
import com.atech.bit.utils.animatedComposable
import com.atech.bit.utils.sharedViewModel


sealed class LibraryRoute(val route: String) {
    data object LibraryManagerScreen : LibraryRoute("library_manager_screen")
    data object LibraryAddEditScreen : LibraryRoute("library_add_edit_screen")
}


fun NavGraphBuilder.libraryGraph(
    navHostController: NavHostController
) {
    navigation(
        startDestination = LibraryRoute.LibraryManagerScreen.route,
        route = RouteName.LIBRARY.value
    ) {
        animatedComposable(route = LibraryRoute.LibraryManagerScreen.route) {
            val viewModel =
                it.sharedViewModel<LibraryManagerViewModel>(navController = navHostController)
            LibraryManagerScreen(
                navHostController = navHostController,
                viewModel = viewModel
            )
        }
        animatedComposable(
            route = LibraryRoute.LibraryAddEditScreen.route
        ) {
            val viewModel =
                it.sharedViewModel<LibraryManagerViewModel>(navController = navHostController)
            AddEditLibraryScreen(
                navController = navHostController,
                viewModel = viewModel
            )
        }
    }

}
