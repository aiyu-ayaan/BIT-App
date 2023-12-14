package com.atech.bit.ui.graph

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.atech.attendance.AttendanceScreenRoutes
import com.atech.attendance.attendanceNavigation
import com.atech.course.CourseScreenRoute
import com.atech.course.courseNavigation
import com.atech.utils.getSimpleName
import com.atech.view_model.SharedViewModel

sealed class MainScreenRoutes(val route: String) {
    data object Attendance :
        MainScreenRoutes(route = getSimpleName(AttendanceScreenRoutes::class.java))

    data object Course : MainScreenRoutes(route = getSimpleName(CourseScreenRoute::class.java))
    data object Home : MainScreenRoutes(route = getSimpleName(HomeScreenRoutes::class.java))

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
        startDestination = MainScreenRoutes.Attendance.route
    ) {
        homeNavigation(
            navHostController,
            communicatorViewModel = communicatorViewModel
        )
        attendanceNavigation(navHostController)
        courseNavigation(
            navHostController,
            communicatorViewModel = communicatorViewModel
        )

    }
}

