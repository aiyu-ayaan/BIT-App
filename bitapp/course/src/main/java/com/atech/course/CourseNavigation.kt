package com.atech.course

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.atech.course.screen.course.CourseScreen
import com.atech.utils.animatedCompose


sealed class CourseNavigation(val route: String) {
    data object CourseScreen : CourseNavigation("course_screen")
    companion object {
        const val COURSE_ROUTE = "course_route"
    }
}
