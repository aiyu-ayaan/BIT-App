package com.atech.bit.ui.screens

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CoPresent
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.rounded.CoPresent
import androidx.compose.material.icons.rounded.CollectionsBookmark
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.atech.bit.R
import com.atech.bit.ui.activity.MainViewModel
import com.atech.bit.ui.comman.NavHeader
import com.atech.bit.ui.comman.singleElement
import com.atech.bit.ui.navigation.BitAppNavigationGraph
import com.atech.bit.ui.navigation.Screen
import com.atech.bit.ui.navigation.listOfFragmentsWithBottomAppBar
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.captionColor
import com.atech.bit.ui.theme.dividerOrCardColor
import com.atech.bit.ui.theme.grid_0_5
import com.atech.bit.ui.theme.grid_2
import com.atech.bit.ui.theme.grid_3
import kotlinx.coroutines.launch


@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    communicatorViewModel: MainViewModel = hiltViewModel()
) {


    val isSearchBarActive = communicatorViewModel.isSearchActive.value

    val drawerSate = rememberDrawerState(initialValue = DrawerValue.Closed)
    val toggleDrawerState by communicatorViewModel.toggleDrawerState
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(toggleDrawerState) {
        toggleDrawerState?.let {
            if (drawerSate.isOpen)
                communicatorViewModel.onEvent(
                    MainViewModel.SharedEvents.ToggleDrawer(DrawerValue.Closed)
                )
            else
                communicatorViewModel.onEvent(
                    MainViewModel.SharedEvents.ToggleDrawer(DrawerValue.Open)
                )
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
            ) {
                coroutineScope.launch {
                    setDrawerState(
                        drawerSate,
                        toggleDrawerState!!
                    )
                }
                communicatorViewModel.onEvent(
                    MainViewModel.SharedEvents.ToggleDrawer(DrawerValue.Closed)
                )
            }
        },
    ) {
        Scaffold(modifier = modifier, bottomBar = {
            NavBar(
                navController = navController,
                isSearchBarActive = isSearchBarActive
            )
        }) {
            Column(
                modifier = Modifier.padding(it)
            ) {
                BitAppNavigationGraph(
                    navHostController = navController,
                    communicatorViewModel = communicatorViewModel
                )
            }
        }
    }
}


val navDrawerItem = listOf(
    null to listOf(
        NavDrawer(
            title = R.string.git,
            selectedIcon = R.drawable.ic_read_me,
            link = R.string.github_link
        ), NavDrawer(
            title = R.string.whats_new,
            selectedIcon = R.drawable.ic_release_notes,
        ), NavDrawer(
            title = R.string.issue,
            selectedIcon = R.drawable.ic_issue,
            link = R.string.issue_link
        )
    ), "Erp & Classes" to listOf(
        NavDrawer(
            title = R.string.erp,
            selectedIcon = R.drawable.ic_web,
            link = R.string.erp_link
        ), NavDrawer(
            title = R.string.administration,
            selectedIcon = R.drawable.ic_admin,
            route = Screen.AdministrationScreen.route
        ), NavDrawer(
            title = R.string.library,
            selectedIcon = R.drawable.ic_library,
            route = Screen.LibraryScreen.route
        ), NavDrawer(
            title = R.string.cgpa_calculator,
            selectedIcon = R.drawable.ic_cgpa_calculator,
            route = Screen.CgpaScreen.route
        ), NavDrawer(
            title = R.string.holidays,
            selectedIcon = R.drawable.ic_holiday,
            route = Screen.HolidayScreen.route
        )
    ), "Societies & Events" to listOf(
        NavDrawer(
            title = R.string.societies,
            selectedIcon = R.drawable.ic_society,
            route = Screen.SocietyScreen.route
        ), NavDrawer(
            title = R.string.events,
            selectedIcon = R.drawable.ic_event,
            route = Screen.EventScreen.route
        )
    ), "Communication" to listOf(
        NavDrawer(
            title = R.string.connect,
            selectedIcon = R.drawable.ic_cwu,
        ), NavDrawer(
            title = R.string.feedback,
            selectedIcon = R.drawable.ic_mail,
        )
    ), "Share & Rate" to listOf(
        NavDrawer(
            title = R.string.share,
            selectedIcon = R.drawable.ic_share,
        ), NavDrawer(
            title = R.string.rate_us,
            selectedIcon = R.drawable.ic_star,
        )
    ), "App Setting" to listOf(
        NavDrawer(
            title = R.string.settings,
            selectedIcon = R.drawable.outline_settings_24,
            route = Screen.SettingsScreen.route
        )
    )
)

@Composable
fun NavDrawer(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    closeAction: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    DismissibleDrawerSheet(
        modifier = modifier,
        drawerContainerColor = Color(
            color()
        ),
        drawerShape = RoundedCornerShape(grid_2)
    ) {
        LazyColumn {
            singleElement (
                "Header"
            ){
                NavHeader()
            }
            items(navDrawerItem) {
                it.first?.let { title ->
                    Text(
                        text = title,
                        modifier = Modifier.padding(start = grid_2, bottom = grid_0_5),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.captionColor
                    )
                }
                it.second.forEach { navBarModel ->
                    drawerItem(
                        screen = navBarModel,
                        currentDestination = currentDestination,
                        navController = navController,
                        closeAction = closeAction
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
    navController: NavHostController,
    onLinkClick: (String) -> Unit = {},
    closeAction: () -> Unit
) = this.apply {
    val context = LocalContext.current
    NavigationDrawerItem(
        modifier = Modifier
            .padding(
                NavigationDrawerItemDefaults.ItemPadding
            ),
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color(
                color()
            )
        ),
        label = {
            Text(
                text = stringResource(id = screen.title)
            )
        },
        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
        onClick = {
            if (screen.link != null) {
                onLinkClick.invoke(
                    context
                        .getString(screen.link)
                )
                return@NavigationDrawerItem
            }
            if (screen.route == "")
                return@NavigationDrawerItem
            closeAction()
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
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

@Composable
private fun color() = ColorUtils.blendARGB(
    MaterialTheme.colorScheme.surface.toArgb(),
    MaterialTheme.colorScheme.primary.toArgb(),
    .09f
)


val navBarItems = listOf(
    NavBarModel(
        title = R.string.home,
        selectedIcon = Icons.Rounded.Dashboard,
        unSelectedIcon = Icons.Outlined.Dashboard,
        route = Screen.HomeScreen.route
    ), NavBarModel(
        title = R.string.course,
        selectedIcon = Icons.Rounded.CollectionsBookmark,
        unSelectedIcon = Icons.Outlined.CollectionsBookmark,
        route = Screen.CourseScreen.route
    ), NavBarModel(
        title = R.string.attendance,
        unSelectedIcon = Icons.Outlined.CoPresent,
        selectedIcon = Icons.Rounded.CoPresent,
        route = Screen.AttendanceScreen.route
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
            modifier = modifier.fillMaxWidth(),
            contentColor = Color(
                ColorUtils.blendARGB(
                    MaterialTheme.colorScheme.surface.toArgb(),
                    Color.Gray.toArgb(),
                    .6f
                )
            ),
            windowInsets = WindowInsets.navigationBars
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
    val route: String = "",
    @StringRes val link: Int? = null
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