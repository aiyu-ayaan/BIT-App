/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.atech.bit.ui.screens.attendance.addedit.components.AddEditAttendanceScreen
import com.atech.bit.ui.screens.attendance.attendance_screen.component.AttendanceScreen
import com.atech.bit.utils.animatedComposable
import com.atech.bit.utils.fadeThroughComposable


sealed class AttendanceRoute(val route: String) {
    data object AttendanceScreen : AttendanceRoute("attendance_screen")
    data object AddEditAttendanceScreen : AttendanceRoute("add_edit_attendance_screen")
}

fun NavGraphBuilder.attendanceGraph(
    navHostController: NavHostController,
) = this.apply {
    navigation(
        startDestination = AttendanceRoute.AttendanceScreen.route,
        route = RouteName.ATTENDANCE.value
    ) {
        fadeThroughComposable(
            route = AttendanceRoute.AttendanceScreen.route
        ) {
            AttendanceScreen(
                navController = navHostController
            )
        }
        animatedComposable(
            route = AttendanceRoute.AddEditAttendanceScreen.route + "?attendanceId={attendanceId}"
                    + "&fromAddFromSyllabus={fromAddFromSyllabus}",
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
