package com.atech.bit.ui.graph

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.atech.attendance.AttendanceScreenRoutes
import com.atech.attendance.attendanceNavigation
import com.atech.bit.ui.screen.holiday.compose.HolidayScreen
import com.atech.course.CourseScreenRoute
import com.atech.course.courseNavigation
import com.atech.utils.animatedCompose
import com.atech.utils.getSimpleName
import com.atech.view_model.SharedViewModel

sealed class MainScreenRoutes(val route: String) {
    data object Attendance :
        MainScreenRoutes(route = getSimpleName(AttendanceScreenRoutes::class.java))

    data object Course : MainScreenRoutes(route = getSimpleName(CourseScreenRoute::class.java))
    data object Home : MainScreenRoutes(route = getSimpleName(HomeScreenRoutes::class.java))
    data object Holiday : MainScreenRoutes(route = "holiday")
}


val listOfFragmentsWithBottomAppBar = listOf(
    HomeScreenRoutes.HomeScreen.route,
    CourseScreenRoute.CourseScreen.route
)

@Composable
fun HomeNavigation(
    navHostController: NavHostController,
    communicatorViewModel: SharedViewModel = hiltViewModel()
) {
    NavHost(
        navController = navHostController,
        startDestination = MainScreenRoutes.Home.route
    ) {
        homeNavigation(
            navHostController,
            communicatorViewModel = communicatorViewModel
        )
        attendanceNavigation(
            navHostController = navHostController,
            communicatorViewModel = communicatorViewModel
        )
        courseNavigation(
            navHostController,
            communicatorViewModel = communicatorViewModel
        )
        animatedCompose(
            route = MainScreenRoutes.Holiday.route
        ) {
            HolidayScreen(
                navController = navHostController,
            )
        }
    }
}

