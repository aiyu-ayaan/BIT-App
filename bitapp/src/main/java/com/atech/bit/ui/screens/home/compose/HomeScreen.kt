package com.atech.bit.ui.screens.home.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.atech.bit.ui.activity.MainViewModel
import com.atech.bit.ui.activity.toggleDrawer
import com.atech.bit.ui.comman.BottomPadding
import com.atech.bit.ui.comman.ChooseSemBottomSheet
import com.atech.bit.ui.comman.DevNote
import com.atech.bit.ui.comman.GITHUB_LINK
import com.atech.bit.ui.comman.ImageIconButton
import com.atech.bit.ui.comman.singleElement
import com.atech.bit.ui.navigation.CourseScreenRoute
import com.atech.bit.ui.navigation.EventRoute
import com.atech.bit.ui.navigation.HomeScreenRoutes
import com.atech.bit.ui.navigation.Screen
import com.atech.bit.ui.screens.course.screen.sem_choose.offlineDataSource
import com.atech.bit.ui.screens.course.screen.sem_choose.onlineDataSource
import com.atech.bit.ui.screens.home.HomeScreenEvents
import com.atech.bit.ui.screens.home.HomeViewModel
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.grid_1
import com.atech.core.usecase.SyllabusUIModel
import com.atech.core.utils.openLinks

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    communicatorViewModel: MainViewModel,
    navController: NavController = rememberNavController(),
    viewModel: HomeViewModel = hiltViewModel()
) {
    val isSearchBarActive = communicatorViewModel.isSearchActive.value
    val isOnlineEnable by viewModel.isOnlineSyllabusEnable
    val theory = viewModel.theory.collectAsLazyPagingItems()
    val lab = viewModel.lab.collectAsLazyPagingItems()
    val pe = viewModel.pe.collectAsLazyPagingItems()
    val onlineData = viewModel.onlineSyllabus.value
    val holiday = viewModel.holidays.value
    val events = viewModel.events.value
    val cgpa = viewModel.currentCgpa.value
    val context = LocalContext.current
    val courseDetails by communicatorViewModel.courseDetail

    var isChooseSemSelected by rememberSaveable {
        mutableStateOf(false)
    }
    Scaffold(modifier = modifier, topBar = {
        var query by remember { mutableStateOf("") }
        SearchToolBar(query = query,
            onQueryChange = { query = it },
            active = isSearchBarActive,
            onActiveChange = { communicatorViewModel.onEvent(MainViewModel.SharedEvents.ToggleSearchActive) },
            onTrailingIconClick = {
                communicatorViewModel.onEvent(MainViewModel.SharedEvents.ToggleSearchActive)
            },
            onLeadingIconClick = {
                communicatorViewModel.onEvent(
                    MainViewModel.SharedEvents.ToggleDrawer(
                        toggleDrawer(communicatorViewModel)
                    )
                )
            },
            onNotificationClick = {
                navController.navigate(
                    HomeScreenRoutes.NoticeScreen.route
                )
            })
    }) {
        LazyColumn(
            modifier = Modifier.consumeWindowInsets(it)
                .animateContentSize(),
            contentPadding = it
        ) {
            singleElement(key = "Header") {
                HeaderCompose(isEnable = isOnlineEnable,
                    onEnableChange = { currentState ->
                        viewModel.onEvent(
                            HomeScreenEvents.ToggleOnlineSyllabusClick(
                                currentState
                            )
                        )
                    },
                    onSettingClick = {
                        isChooseSemSelected = !isChooseSemSelected
                    }
                )
            }
            if (isOnlineEnable) onlineDataSource(onlineData.first,
                onlineData.second,
                onlineData.third,
                onClick = { model ->
                    navigateToViewSyllabus(navController, viewModel, true, model)
                })
            else offlineDataSource(theoryData = theory,
                labData = lab,
                peData = pe,
                onClick = { model ->
                    navigateToViewSyllabus(navController, viewModel, false, model)
                })
            showHoliday(holiday, endItem = "View All", endItemClick = {
                navController.navigate(
                    Screen.HolidayScreen.route
                )
            })
            showEvents(items = events,
                getAttach = viewModel.firebaseCase.getAttach,
                onClick = { event ->
                    navController.navigate(
                        EventRoute.DetailScreen.route + "?eventId=${event.created}"
                    )
                })
            singleElement(key = "CGPA") {
                CgpaHomeElement(
                    cgpa = cgpa
                )
            }
            singleElement(key = "DevNote") {
                DevNote(onClick = {
                    GITHUB_LINK.openLinks(context)
                })
            }
            singleElement(key = "BottomPadding") { BottomPadding() }
        }
        if (isChooseSemSelected) {
            ChooseSemBottomSheet(
                model = courseDetails,
                sem = viewModel.sem.value,
                course = viewModel.course.value,
                onDismissRequest = {
                    isChooseSemSelected = false
                },
                onSaveClick = { it1 ->
                    viewModel.onEvent(
                        HomeScreenEvents.OnCourseChange(it1)
                    )
                }
            )
        }
    }
}

private fun navigateToViewSyllabus(
    navController: NavController,
    viewModel: HomeViewModel,
    isOnlineEnable: Boolean,
    model: SyllabusUIModel
) {
    navController.navigate(
        CourseScreenRoute.ViewSubjectScreen.route + "?course=${(viewModel.course.value).lowercase()}" + "&courseSem=${if (isOnlineEnable) "${viewModel.course.value}${viewModel.sem.value}".lowercase() else model.openCode}" + "&subject=${model.subject}" + "&isOnline=$isOnlineEnable"
    )
}

@Composable
private fun HeaderCompose(
    modifier: Modifier = Modifier,
    isEnable: Boolean = false,
    onEnableChange: (Boolean) -> Unit = {},
    onEditClick: () -> Unit = {},
    onSettingClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = grid_1, top = grid_1),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = "Your Subjects",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Switch(colors = SwitchDefaults.colors(
            checkedIconColor = MaterialTheme.colorScheme.primary
        ), checked = isEnable, onCheckedChange = {
            onEnableChange.invoke(it)
        }, thumbContent = {
            Icon(
                modifier = Modifier.size(SwitchDefaults.IconSize),
                imageVector = if (isEnable) Icons.Outlined.Wifi
                else Icons.Outlined.WifiOff,
                contentDescription = null
            )
        })
        AnimatedVisibility(visible = !isEnable) {
            ImageIconButton(
                icon = Icons.Outlined.Edit,
                tint = MaterialTheme.colorScheme.primary,
                onClick = onEditClick
            )
        }
        ImageIconButton(
            icon = Icons.Outlined.Settings,
            tint = MaterialTheme.colorScheme.primary,
            onClick = onSettingClick
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    BITAppTheme {
        HeaderCompose()
    }
}