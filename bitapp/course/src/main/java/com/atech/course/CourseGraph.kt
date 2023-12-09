package com.atech.course

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.atech.course.screen.course.CourseScreen
import com.atech.utils.animatedCompose
import com.atech.utils.getSimpleName


sealed class CourseScreenRoute(val route: String) {
    data object CourseScreen : CourseScreenRoute("course_screen")

}

fun NavGraphBuilder.courseNavigation(
    navController: NavController
) {
    navigation(
        startDestination = CourseScreenRoute.CourseScreen.route,
        route = getSimpleName(CourseScreenRoute::class.java)
    ) {
        animatedCompose(
            route = CourseScreenRoute.CourseScreen.route
        ) {
            CourseScreen()
        }
    }
}