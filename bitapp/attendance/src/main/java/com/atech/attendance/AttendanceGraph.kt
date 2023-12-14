package com.atech.attendance

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.atech.attendance.screen.add_edit.components.AddEditAttendanceScreen
import com.atech.attendance.screen.attendance.compose.AttendanceScreen
import com.atech.utils.animatedCompose
import com.atech.utils.getSimpleName


sealed class AttendanceScreenRoutes(val route: String) {
    data object AttendanceScreen : AttendanceScreenRoutes("attendance_screen")
    data object AddEditAttendanceScreen : AttendanceScreenRoutes("add_edit_attendance_screen")
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
            AttendanceScreen(
                navController = navHostController
            )
        }
        animatedCompose(
            route = AttendanceScreenRoutes.AddEditAttendanceScreen.route + "?attendanceId={attendanceId}",
            arguments = listOf(
                navArgument("attendanceId") {
                    defaultValue = -1
                    type = NavType.IntType
                }
            )
        ) {
            AddEditAttendanceScreen(
                navController = navHostController
            )
        }
    }
}

