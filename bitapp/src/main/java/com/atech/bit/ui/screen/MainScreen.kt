package com.atech.bit.ui.screen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.atech.bit.ui.graph.HomeNavigation
import com.atech.bit.ui.graph.MainScreenRoutes
import com.atech.bit.ui.graph.listOfFragmentsWithBottomAppBar
import com.atech.theme.BITAppTheme
import com.atech.theme.R
import com.atech.theme.grid_3


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier, navController: NavHostController = rememberNavController()
) {
    val topBarScrollState = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(modifier = modifier, bottomBar = {
        NavBar(
            navController = navController
        )
    }) {
        Column(
            modifier = Modifier
                .padding(it)
                .nestedScroll(topBarScrollState.nestedScrollConnection)
        ) {
            HomeNavigation(navHostController = navController)
        }
    }
}

@Composable
fun NavBar(
    modifier: Modifier = Modifier, navController: NavHostController
) {

    val navBarItems = listOf(
        NavBarModel(
            title = R.string.home,
            selectedIcon = com.atech.bit.R.drawable.ic_home_filled,
            unSelectedIcon = com.atech.bit.R.drawable.ic_home,
            route = MainScreenRoutes.Home.route
        ), NavBarModel(
            title = R.string.course,
            selectedIcon = com.atech.bit.R.drawable.ic_syllabus_filled,
            unSelectedIcon = com.atech.bit.R.drawable.ic_syllabus,
            route = MainScreenRoutes.Course.route
        ), NavBarModel(
            title = R.string.attendance,
            selectedIcon = com.atech.bit.R.drawable.ic_attendance,
            route = MainScreenRoutes.Attendance.route
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isTheir = listOfFragmentsWithBottomAppBar.any { it == currentDestination?.route }
    val density = LocalDensity.current
    AnimatedVisibility(
        visible = isTheir,
        enter = slideInVertically {
            with(density) { -40.dp.roundToPx() }
        } + expandVertically(
            expandFrom = Alignment.Top
        ) + fadeIn(
            initialAlpha = 0.3f
        ),
        exit = slideOutVertically() + shrinkVertically() + fadeOut()
    ) {
        NavigationBar(
            modifier = modifier.fillMaxWidth(), contentColor = MaterialTheme.colorScheme.primary
        ) {
            navBarItems.forEachIndexed { index, navBarModel ->
                AddItem(
                    screen = navBarModel,
                    currentDestination = currentDestination,
                    index = index,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: NavBarModel,
    currentDestination: NavDestination?,
    index: Int,
    navController: NavHostController
) {
    var selectedItem by rememberSaveable {
        mutableIntStateOf(0)
    }
    NavigationBarItem(icon = {
        if (screen.unSelectedIcon == null) {
            Icon(
                modifier = Modifier.size(grid_3),
                painter = painterResource(id = screen.selectedIcon),
                contentDescription = stringResource(id = screen.title)
            )
            return@NavigationBarItem
        }
        if (selectedItem == index) Icon(
            painter = painterResource(id = screen.selectedIcon),
            contentDescription = stringResource(id = screen.title)
        )
        else Icon(
            painter = painterResource(id = screen.unSelectedIcon),
            contentDescription = stringResource(id = screen.title)
        )
    }, selected = currentDestination?.hierarchy?.any {
        it.route == screen.route
    } == true, onClick = {
        selectedItem = index
        navController.navigate(screen.route) {
            popUpTo(navController.graph.findStartDestination().id)
            launchSingleTop = true
        }
    })
}

data class NavBarModel(
    @StringRes val title: Int,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unSelectedIcon: Int? = null,
    val route: String
)

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    BITAppTheme {

    }
}