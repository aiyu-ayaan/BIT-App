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
import com.atech.bit.ui.activity.main.MainViewModel
import com.atech.bit.ui.screens.MainScreen
import com.atech.bit.ui.screens.administration.AdministrationScreen
import com.atech.bit.ui.screens.cgpa.compose.CgpaScreen
import com.atech.bit.ui.screens.holiday.compose.HolidayScreen
import com.atech.bit.ui.screens.view_image.ViewImageScreen
import com.atech.bit.utils.animatedComposable
import java.io.UnsupportedEncodingException
import java.net.URLEncoder


enum class TopLevelRoute(val route: String) {
    LOGIN("login"), MAIN_SCREEN("main_screen"),
}

sealed class ParentScreenRoutes(val route: String) {
    data object LoginScreen : ParentScreenRoutes(TopLevelRoute.LOGIN.route)
    data object MainScreen : ParentScreenRoutes(TopLevelRoute.MAIN_SCREEN.route)
}

@Composable
fun TopLevelNavigationGraph(
    navHostController: NavHostController,
    communicatorViewModel: MainViewModel,
    startDestination: String = ParentScreenRoutes.LoginScreen.route
) {
    NavHost(
        navController = navHostController, startDestination = startDestination
    ) {
        logInScreenGraph(
            navHostController, communicatorViewModel
        )
        animatedComposable(
            route = ParentScreenRoutes.MainScreen.route
        ) {
            MainScreen(
                communicatorViewModel = communicatorViewModel
            )
        }
    }
}


val listOfFragmentsWithBottomAppBar = listOf(
    CourseScreenRoute.CourseScreen.route, HomeScreenRoutes.HomeScreen.route
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
    data class ViewImageRoute(val imageLink: String = "{imageLink}") : DeepLinkRoutes(
        "$BASE_IN_APP_NAVIGATION_LINK/viewImage/?imageLink=${
            imageLink.encodeUrl().replaceAmpersandWithAsterisk()
        }"
    )

    data class EventDetailScreen(val eventId: String = "{eventId}") : DeepLinkRoutes(
        "$BASE_IN_APP_NAVIGATION_LINK/events/eventDetails/?eventId=${
            eventId
        }"
    )

    data class NoticeDetailScreen(val noticeId: String = "{noticeId}") : DeepLinkRoutes(
        "$BASE_IN_APP_NAVIGATION_LINK/notice/noticeDetails/?noticeId=${
            noticeId
        }"
    )
}

fun NavController.navigateWithDeepLink(route: DeepLinkRoutes) {
    val action = NavDeepLinkRequest.Builder.fromUri(route.route.toUri()).build()
    this.navigate(action)
}

enum class RouteName(val value: String) {
    ATTENDANCE("attendance"), COURSE("course"), HOME("home"), SOCIETY("society"), LIBRARY("library"), EVENT(
        "event"
    ),
    SETTINGS("settings"), ABOUT_US("about_us"),
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

    data object AboutUsScreen : Screen(RouteName.ABOUT_US.value)
}


@Composable
fun AppNavigationGraph(
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
            navController = navHostController, communicatorViewModel = communicatorViewModel
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
            navHostController = navHostController, mainViewModel = communicatorViewModel
        )
        animatedComposable(route = Screen.ViewImageRoute.route, deepLinks = listOf(navDeepLink {
            uriPattern = DeepLinkRoutes.ViewImageRoute().route
            action = Intent.ACTION_VIEW
        }), arguments = listOf(navArgument("imageLink") {
            type = NavType.StringType
            defaultValue = ""
        })
        ) { backStack ->
            val link = backStack.arguments?.getString("imageLink")?.replaceAsteriskWithAmpersand()
            ViewImageScreen(
                navController = navHostController, link = link ?: ""
            )
        }
        aboutUsNavGraph(navHostController = navHostController)
    }
}


