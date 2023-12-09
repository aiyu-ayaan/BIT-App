package com.atech.attendance

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.atech.attendance.AttendanceNavigation.Companion.ATTENDANCE_ROUTE
import com.atech.attendance.screen.attendance.AttendanceScreen
import com.atech.utils.animatedCompose


sealed class AttendanceNavigation(val route: String) {
    data object AttendanceScreen : AttendanceNavigation("attendance_screen")

    companion object{
        const val ATTENDANCE_ROUTE = "attendance_route"
    }
}

@Composable
fun NavGraphBuilder.attendanceRoute() = this.apply {
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