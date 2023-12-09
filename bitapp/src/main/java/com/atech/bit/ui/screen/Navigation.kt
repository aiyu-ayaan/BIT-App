package com.atech.bit.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import com.atech.attendance.AttendanceNavigation
import com.atech.attendance.AttendanceNavigation.Companion.ATTENDANCE_ROUTE
import com.atech.attendance.screen.attendance.AttendanceScreen
import com.atech.bit.ui.screen.home.HomeScreen
import com.atech.course.CourseNavigation
import com.atech.course.CourseNavigation.Companion.COURSE_ROUTE
import com.atech.course.screen.course.CourseScreen
import com.atech.utils.animatedCompose


sealed class BITAppRoutes(val route: String) {
    data object Attendance : BITAppRoutes(ATTENDANCE_ROUTE)
    data object Course : BITAppRoutes(COURSE_ROUTE)
    data object Home : BITAppRoutes("home_screen")

}

@Composable
fun Navigation(
    navHostController: NavHostController
) {
    NavHost(navController = navHostController, startDestination = BITAppRoutes.Home.route) {
        animatedCompose(
            route = BITAppRoutes.Home.route
        ) {
            HomeScreen()
        }
        navigation(
            startDestination = CourseNavigation.CourseScreen.route,
            route = BITAppRoutes.Course.route
        ) {
            animatedCompose(
                route = CourseNavigation.CourseScreen.route
            ) {
                CourseScreen()
            }
        }
        navigation(
            startDestination = AttendanceNavigation.AttendanceScreen.route,
            route = ATTENDANCE_ROUTE
        ) {
            animatedCompose(
                route = AttendanceNavigation.AttendanceScreen.route
            ) {
                AttendanceScreen()
            }
        }
    }
}

