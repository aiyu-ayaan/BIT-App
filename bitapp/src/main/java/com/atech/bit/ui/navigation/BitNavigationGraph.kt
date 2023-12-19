package com.atech.bit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.atech.bit.ui.activity.MainViewModel
import com.atech.bit.ui.screens.administration.AdministrationScreen
import com.atech.bit.ui.screens.cgpa.compose.CgpaScreen
import com.atech.bit.ui.screens.holiday.compose.HolidayScreen
import com.atech.bit.utils.animatedComposable

val listOfFragmentsWithBottomAppBar = listOf(
    CourseScreenRoute.CourseScreen.route,
    HomeScreenRoutes.HomeScreen.route
)

enum class RouteName(val value: String) {
    ATTENDANCE("attendance"),
    COURSE("course"),
    HOME("home"),
    SOCIETY("society"),
    LIBRARY("library"),
    EVENT("event"),
    SETTINGS("settings")
}


sealed class Screen(val route: String) {
    data object AttendanceScreen : Screen(RouteName.ATTENDANCE.value)

    data object CourseScreen : Screen(RouteName.COURSE.value)

    data object HomeScreen : Screen(RouteName.HOME.value)

    data object SocietyScreen : Screen(RouteName.SOCIETY.value)

    data object HolidayScreen : Screen("holiday")

    data object AdministrationScreen : Screen("administration")

    data object LibraryScreen : Screen(RouteName.LIBRARY.value)

    data object CgpaScreen : Screen("cgpa")

    data object EventScreen : Screen(RouteName.EVENT.value)

    data object SettingsScreen : Screen(RouteName.SETTINGS.value)
}


@Composable
fun BitAppNavigationGraph(
    navHostController: NavHostController,
    communicatorViewModel: MainViewModel,
    startDestination: String = Screen.HomeScreen.route
) {
    NavHost(
        navController = navHostController, startDestination = startDestination
    ) {
        attendanceGraph(
            navHostController = navHostController
        )

        courseGraph(
            navController = navHostController
        )

        homeGraph(
            navController = navHostController,
            communicatorViewModel = communicatorViewModel
        )
        societyGraph(
            navHostController = navHostController
        )
        animatedComposable(
            route = Screen.HolidayScreen.route
        ) {
            HolidayScreen(
                navController = navHostController
            )
        }
        animatedComposable(
            route = Screen.AdministrationScreen.route
        ) {
            AdministrationScreen(
                navController = navHostController
            )
        }
        libraryGraph(navHostController = navHostController)

        animatedComposable(
            route = Screen.CgpaScreen.route
        ) {
            CgpaScreen(navController = navHostController)
        }
        eventGraph(navHostController = navHostController)

        settingNavigationGraph(
            navHostController = navHostController,
            mainViewModel = communicatorViewModel
        )
    }
}


