package com.atech.attendance

import androidx.annotation.Keep
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.atech.attendance.screen.add_edit.components.AddEditAttendanceScreen
import com.atech.attendance.screen.attendance.compose.AttendanceScreen
import com.atech.utils.animatedComposable
import com.atech.utils.fadeThroughComposable
import com.atech.utils.getSimpleName
import com.atech.view_model.SharedViewModel

@Keep
sealed class AttendanceScreenRoutes(val route: String) {
    data object AttendanceScreen : AttendanceScreenRoutes("attendance_screen")
    data object AddEditAttendanceScreen : AttendanceScreenRoutes("add_edit_attendance_screen")
}


fun NavGraphBuilder.attendanceNavigation(
    navHostController: NavHostController,
    communicatorViewModel: SharedViewModel
) {
    navigation(
        startDestination = AttendanceScreenRoutes.AttendanceScreen.route,
        route = getSimpleName(AttendanceScreenRoutes::class.java)
    ) {
        fadeThroughComposable(
            route = AttendanceScreenRoutes.AttendanceScreen.route
        ) {
            AttendanceScreen(
                navController = navHostController,
                communicatorViewModel = communicatorViewModel
            )
        }
        animatedComposable(
            route = AttendanceScreenRoutes.AddEditAttendanceScreen.route + "?attendanceId={attendanceId}"
            +"&fromAddFromSyllabus={fromAddFromSyllabus}",
            arguments = listOf(
                navArgument("attendanceId") {
                    defaultValue = -1
                    type = NavType.IntType
                },
                navArgument("fromAddFromSyllabus") {
                    defaultValue = 0
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

