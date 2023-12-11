package com.atech.course

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.atech.course.screen.course.CourseScreen
import com.atech.course.screen.sem_choose.SemChooseScreen
import com.atech.course.screen.sub_view.ViewSubjectScreen
import com.atech.utils.animatedCompose
import com.atech.utils.getSimpleName
import com.atech.utils.sharedViewModel


sealed class CourseScreenRoute(val route: String) {
    data object CourseScreen : CourseScreenRoute("course_screen")
    data object SemChooseScreen : CourseScreenRoute("sem_choose_screen")

    data object ViewSubjectScreen : CourseScreenRoute("view_subject_screen")
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
            val viewModel = it.sharedViewModel<CourseViewModel>(navController)
            CourseScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        animatedCompose(
            route = CourseScreenRoute.SemChooseScreen.route
        ) {
            val viewModel = it.sharedViewModel<CourseViewModel>(navController = navController)
            SemChooseScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        animatedCompose(
            route = CourseScreenRoute.ViewSubjectScreen.route +
                    "?course={course}&courseSem={courseSem}" +
                    "&subject={subject}&isOnline={isOnline}",
            arguments = listOf(
                navArgument("course") {
                    type = NavType.StringType
                },
                navArgument("courseSem") {
                    type = NavType.StringType
                },
                navArgument("subject") {
                    type = NavType.StringType
                },
                navArgument("isOnline") {
                    type = NavType.BoolType
                    defaultValue = true
                }
            )
        ) {
            ViewSubjectScreen(
                navController = navController
            )
        }
    }
}