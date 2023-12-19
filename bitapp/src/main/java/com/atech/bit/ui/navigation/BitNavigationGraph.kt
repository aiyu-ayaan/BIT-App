package com.atech.bit.ui.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.atech.bit.ui.activity.MainViewModel
import com.atech.bit.ui.screens.administration.AdministrationScreen
import com.atech.bit.ui.screens.cgpa.compose.CgpaScreen
import com.atech.bit.ui.screens.holiday.compose.HolidayScreen
import com.atech.bit.ui.screens.view_image.ViewImageScreen
import com.atech.bit.utils.animatedComposable
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

val listOfFragmentsWithBottomAppBar = listOf(
    CourseScreenRoute.CourseScreen.route,
    HomeScreenRoutes.HomeScreen.route
)

const val BASE_IN_APP_NAVIGATION_LINK = "bitapp://bit.aiyu/"

fun String.replaceAmpersandWithAsterisk(): String {
    return this.replace("&", "*")
}

fun String.replaceAsteriskWithAmpersand(): String {
    return this.replace("*", "&")
}

fun String.encodeUrl(): String {
    return try {
        URLEncoder.encode(this, "UTF-8")
    } catch (e: UnsupportedEncodingException) {
        ""
    }
}

sealed class DeepLinkRoutes(val route: String) {
    data class ViewImageRoute(val imageLink: String = "{imageLink}") :
        DeepLinkRoutes(
            "$BASE_IN_APP_NAVIGATION_LINK/viewImage/?imageLink=${
                imageLink.encodeUrl().replaceAmpersandWithAsterisk()
            }"
        )
}

fun NavController.navigateWithDeepLink(route: DeepLinkRoutes) {
    val action = NavDeepLinkRequest.Builder
        .fromUri(route.route.toUri())
        .build()
    this.navigate(action)
}

enum class RouteName(val value: String) {
    ATTENDANCE("attendance"),
    COURSE("course"),
    HOME("home"),
    SOCIETY("society"),
    LIBRARY("library"),
    EVENT("event"),
    SETTINGS("settings")
}


sealed class Screen(val route: String) {
    data object AttendanceScreen : Screen(RouteName.ATTENDANCE.value)

    data object CourseScreen : Screen(RouteName.COURSE.value)

    data object HomeScreen : Screen(RouteName.HOME.value)

    data object SocietyScreen : Screen(RouteName.SOCIETY.value)

    data object HolidayScreen : Screen("holiday")

    data object AdministrationScreen : Screen("administration")

    data object LibraryScreen : Screen(RouteName.LIBRARY.value)

    data object CgpaScreen : Screen("cgpa")

    data object EventScreen : Screen(RouteName.EVENT.value)

    data object SettingsScreen : Screen(RouteName.SETTINGS.value)

    data object ViewImageRoute : Screen("view_image_route")
}


@Composable
fun BitAppNavigationGraph(
    navHostController: NavHostController,
    communicatorViewModel: MainViewModel,
    startDestination: String = Screen.HomeScreen.route
) {
    NavHost(
        navController = navHostController, startDestination = startDestination
    ) {
        attendanceGraph(
            navHostController = navHostController
        )

        courseGraph(
            navController = navHostController
        )

        homeGraph(
            navController = navHostController,
            communicatorViewModel = communicatorViewModel
        )
        societyGraph(
            navHostController = navHostController
        )
        animatedComposable(
            route = Screen.HolidayScreen.route
        ) {
            HolidayScreen(
                navController = navHostController
            )
        }
        animatedComposable(
            route = Screen.AdministrationScreen.route
        ) {
            AdministrationScreen(
                navController = navHostController
            )
        }
        libraryGraph(navHostController = navHostController)

        animatedComposable(
            route = Screen.CgpaScreen.route
        ) {
            CgpaScreen(navController = navHostController)
        }
        eventGraph(navHostController = navHostController)

        settingNavigationGraph(
            navHostController = navHostController,
            mainViewModel = communicatorViewModel
        )
        animatedComposable(
            route = Screen.ViewImageRoute.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = DeepLinkRoutes.ViewImageRoute().route
                    action = Intent.ACTION_VIEW
                }
            ),
            arguments = listOf(
                navArgument("imageLink") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStack ->
            val link = backStack.arguments?.getString("imageLink")?.replaceAsteriskWithAmpersand()
            ViewImageScreen(
                navController = navHostController,
                link = link ?: ""
            )
        }
    }
}


