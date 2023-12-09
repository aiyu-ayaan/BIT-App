package com.atech.attendance

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.atech.attendance.screen.attendance.AttendanceScreen
import com.atech.utils.animatedCompose
import com.atech.utils.getSimpleName


sealed class AttendanceScreenRoutes(val route: String) {
    data object AttendanceScreen : AttendanceScreenRoutes("attendance_screen")

}


fun NavGraphBuilder.attendanceNavigation(
    navHostController: NavHostController
) {
    navigation(
        startDestination = AttendanceScreenRoutes.AttendanceScreen.route,
        route = getSimpleName(AttendanceScreenRoutes::class.java)
    ) {
        animatedCompose(
            route = AttendanceScreenRoutes.AttendanceScreen.route
        ) {
            AttendanceScreen()
        }
    }
}

