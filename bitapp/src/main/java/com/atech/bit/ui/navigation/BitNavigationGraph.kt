package com.atech.bit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.atech.bit.ui.activity.MainViewModel
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
    Society("society"),
}


sealed class Screen(val route: String) {
    data object AttendanceScreen : Screen(RouteName.ATTENDANCE.value)

    data object CourseScreen : Screen(RouteName.COURSE.value)

    data object HomeScreen : Screen(RouteName.HOME.value)

    data object SocietyScreen : Screen(RouteName.Society.value)

    data object HolidayScreen : Screen("holiday")
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
        ){
            HolidayScreen(
                navController = navHostController
            )
        }
    }
}


