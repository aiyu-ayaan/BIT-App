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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CoPresent
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.DashboardCustomize
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.CoPresent
import androidx.compose.material.icons.rounded.CollectionsBookmark
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.DashboardCustomize
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.atech.theme.captionColor
import com.atech.theme.grid_0_5
import com.atech.theme.grid_2
import com.atech.theme.grid_3
import com.atech.view_model.SharedEvents
import com.atech.view_model.SharedViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    communicatorViewModel: SharedViewModel = hiltViewModel()
) {

    val isSearchBarActive = communicatorViewModel.isSearchActive.value
    val toggleDrawerState by communicatorViewModel.toggleDrawerState
    val drawerSate = rememberDrawerState(initialValue = DrawerValue.Closed)
    LaunchedEffect(toggleDrawerState) {
        toggleDrawerState?.let {
            if (drawerSate.isOpen)
                communicatorViewModel.onEvent(SharedEvents.ToggleDrawer(DrawerValue.Closed))
            else
                communicatorViewModel.onEvent(SharedEvents.ToggleDrawer(DrawerValue.Open))
            setDrawerState(
                drawerSate,
                toggleDrawerState!!
            )
        }
    }

    DismissibleNavigationDrawer(
        drawerState = drawerSate,
        drawerContent = {
            NavDrawer(
                navController = navController
            )
        },
    ) {
        Scaffold(modifier = modifier, bottomBar = {
            NavBar(
                navController = navController, isSearchBarActive = isSearchBarActive
            )
        }) {
            Column(
                modifier = Modifier.padding(it)
            ) {
                HomeNavigation(
                    navHostController = navController, communicatorViewModel = communicatorViewModel
                )
            }
        }
    }
}


val navDrawerItem = listOf(
    null to listOf(
        NavDrawer(
            title = R.string.git,
            selectedIcon = com.atech.bit.R.drawable.ic_read_me,
        ), NavDrawer(
            title = R.string.whats_new,
            selectedIcon = com.atech.bit.R.drawable.ic_release_notes,
        ), NavDrawer(
            title = R.string.issue,
            selectedIcon = com.atech.bit.R.drawable.ic_issue,
        )
    ), "Erp & Classes" to listOf(
        NavDrawer(
            title = R.string.erp,
            selectedIcon = com.atech.bit.R.drawable.ic_web,
        ), NavDrawer(
            title = R.string.administration,
            selectedIcon = com.atech.bit.R.drawable.ic_admin,
        ), NavDrawer(
            title = R.string.library,
            selectedIcon = com.atech.bit.R.drawable.ic_library,
        ), NavDrawer(
            title = R.string.cgpa_calculator,
            selectedIcon = com.atech.bit.R.drawable.ic_cgpa_calculator,
        ), NavDrawer(
            title = R.string.holidays,
            selectedIcon = com.atech.bit.R.drawable.ic_holiday,
        )
    ), "Societies & Events" to listOf(
        NavDrawer(
            title = R.string.societies,
            selectedIcon = com.atech.bit.R.drawable.ic_society,
        ), NavDrawer(
            title = R.string.events,
            selectedIcon = com.atech.bit.R.drawable.ic_event,
        )
    ), "Communication" to listOf(
        NavDrawer(
            title = R.string.connect,
            selectedIcon = com.atech.bit.R.drawable.ic_cwu,
        ), NavDrawer(
            title = R.string.feedback,
            selectedIcon = com.atech.bit.R.drawable.ic_mail,
        )
    ), "Share & Rate" to listOf(
        NavDrawer(
            title = R.string.share,
            selectedIcon = com.atech.bit.R.drawable.ic_share,
        ), NavDrawer(
            title = R.string.rate_us,
            selectedIcon = com.atech.bit.R.drawable.ic_star,
        )
    ), "App Setting" to listOf(
        NavDrawer(
            title = R.string.display,
            selectedIcon = com.atech.bit.R.drawable.round_aod_24,
        ), NavDrawer(
            title = R.string.notification,
            selectedIcon = com.atech.bit.R.drawable.outline_notifications_active_24,
        )
    )
)

@Composable
fun NavDrawer(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    DismissibleDrawerSheet(
        modifier = modifier,
        drawerContainerColor = MaterialTheme.colorScheme.primaryContainer,
        drawerShape = RoundedCornerShape(grid_2)
    ) {
        LazyColumn {
            items(navDrawerItem) {
                it.first?.let { title ->
                    Text(
                        text = title,
                        modifier = Modifier.padding(start = grid_2, bottom = grid_0_5),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.captionColor
                    )
                }
                it.second.forEachIndexed { index, navBarModel ->
                    drawerItem(
                        screen = navBarModel,
                        currentDestination = currentDestination,
                        index = index,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.drawerItem(
    screen: NavDrawer,
    currentDestination: NavDestination?,
    index: Int,
    navController: NavHostController
) = this.apply {
    NavigationDrawerItem(
        modifier = Modifier
            .padding(
                NavigationDrawerItemDefaults.ItemPadding
            ),
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        label = {
            Text(
                text = stringResource(id = screen.title)
            )
        },
        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
        onClick = {

//            navController.navigate(screen.route) {
//                popUpTo(navController.graph.findStartDestination().id)
//                launchSingleTop = true
//            }
        },
        icon = {
            Icon(
                modifier = Modifier.size(grid_3),
                painter = painterResource(id = screen.selectedIcon),
                contentDescription = stringResource(id = screen.title),
                tint = MaterialTheme.colorScheme.primary
            )
        })
}


val navBarItems = listOf(
    NavBarModel(
        title = R.string.home,
        selectedIcon = Icons.Rounded.Dashboard,
        unSelectedIcon = Icons.Outlined.Dashboard,
        route = MainScreenRoutes.Home.route
    ), NavBarModel(
        title = R.string.course,
        selectedIcon = Icons.Rounded.CollectionsBookmark,
        unSelectedIcon = Icons.Outlined.CollectionsBookmark,
        route = MainScreenRoutes.Course.route
    ), NavBarModel(
        title = R.string.attendance,
        unSelectedIcon = Icons.Outlined.CoPresent,
        selectedIcon = Icons.Rounded.CoPresent,
        route = MainScreenRoutes.Attendance.route
    )
)

@Composable
fun NavBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    isSearchBarActive: Boolean = false
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isTheir = listOfFragmentsWithBottomAppBar.any { it == currentDestination?.route }
    val density = LocalDensity.current
    AnimatedVisibility(visible = isTheir && !isSearchBarActive, enter = slideInVertically {
        with(density) { -40.dp.roundToPx() }
    } + expandVertically(
        expandFrom = Alignment.Top
    ) + fadeIn(
        initialAlpha = 0.3f
    ), exit = slideOutVertically() + shrinkVertically() + fadeOut()) {
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
        mutableIntStateOf(1)
    }
    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
    NavigationBarItem(
        icon = {
            if (screen.unSelectedIcon == null) {
                Icon(
                    modifier = Modifier.size(grid_3),
                    imageVector = screen.selectedIcon,
                    contentDescription = stringResource(id = screen.title)
                )
                return@NavigationBarItem
            }
            if (isSelected) Icon(
                imageVector = screen.selectedIcon,
                contentDescription = stringResource(id = screen.title)
            )
            else Icon(
                imageVector = screen.unSelectedIcon,
                contentDescription = stringResource(id = screen.title)
            )
        },
        selected = isSelected,
        onClick = {
            selectedItem = index
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        })
}

suspend fun setDrawerState(
    drawerState: DrawerState,
    value: DrawerValue
) {
    if (value == DrawerValue.Closed) {
        drawerState.close()

    } else
        drawerState.open()
}

data class NavDrawer(
    @StringRes val title: Int,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unSelectedIcon: Int? = null,
    val route: String = ""
)

data class NavBarModel(
    @StringRes val title: Int,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector? = null,
    val route: String = ""
)

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    BITAppTheme {

    }
}