package com.atech.bit.ui.screens.home.compose

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
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
import androidx.compose.material.icons.outlined.NotificationsOff
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.atech.bit.R
import com.atech.bit.ui.activity.main.MainViewModel
import com.atech.bit.ui.activity.main.toggleDrawer
import com.atech.bit.ui.comman.BottomPadding
import com.atech.bit.ui.comman.ChooseSemBottomSheet
import com.atech.bit.ui.comman.DevNote
import com.atech.bit.ui.comman.GITHUB_LINK
import com.atech.bit.ui.comman.ImageIconButton
import com.atech.bit.ui.comman.PreferenceCard
import com.atech.bit.ui.comman.singleElement
import com.atech.bit.ui.navigation.CourseScreenRoute
import com.atech.bit.ui.navigation.EventRoute
import com.atech.bit.ui.navigation.HomeScreenRoutes
import com.atech.bit.ui.navigation.Screen
import com.atech.bit.ui.navigation.replaceAmpersandWithAsterisk
import com.atech.bit.ui.screens.course.screen.sem_choose.offlineDataSource
import com.atech.bit.ui.screens.course.screen.sem_choose.onlineDataSource
import com.atech.bit.ui.screens.home.HomeScreenEvents
import com.atech.bit.ui.screens.home.HomeViewModel
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.ui.theme.grid_2
import com.atech.core.datasource.firebase.firestore.EventModel
import com.atech.core.usecase.SyllabusUIModel
import com.atech.core.utils.Permissions
import com.atech.core.utils.connectivity.ConnectivityObserver
import com.atech.core.utils.isAPI33AndUp
import com.atech.core.utils.openLinks
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    communicatorViewModel: MainViewModel,
    navController: NavController = rememberNavController(),
    viewModel: HomeViewModel = hiltViewModel()
) {
    val isSearchBarActive = communicatorViewModel.isSearchActive.value
    val homeScreenState by viewModel.homeScreenState
    val isOnlineEnable = homeScreenState.isOnlineSyllabusEnable
    val theory = homeScreenState.offTheory.collectAsLazyPagingItems()
    val lab = homeScreenState.offLab.collectAsLazyPagingItems()
    val pe = homeScreenState.offPractical.collectAsLazyPagingItems()
    val onlineData = homeScreenState.onlineSyllabus
    val holiday = homeScreenState.holiday
    val events = homeScreenState.events.value
    val cgpa = homeScreenState.currentCgpa
    val context = LocalContext.current
    val courseDetails by communicatorViewModel.courseDetail
    var query by rememberSaveable { mutableStateOf("") }

    val profileUrl by communicatorViewModel.profileLink

    var isChooseSemSelected by rememberSaveable {
        mutableStateOf(false)
    }
    var isProfileDialogVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var permissionState: PermissionState? = null
    isAPI33AndUp {
        permissionState = rememberPermissionState(permission = Permissions.NOTIFICATION.value)
        LaunchedEffect(permissionState?.status) {
            when (permissionState?.status) {
                is PermissionStatus.Denied -> communicatorViewModel.onNotificationPrefItemVisibleChange(
                    false
                )

                PermissionStatus.Granted -> communicatorViewModel.onNotificationPrefItemVisibleChange(
                    true
                )

                null -> communicatorViewModel.onNotificationPrefItemVisibleChange(false)
            }
        }
    }
    Scaffold(modifier = modifier, topBar = {
        isAPI33AndUp {
            permissionState?.launchPermissionRequest()
        }
        SearchToolBar(query = query,
            onQueryChange = {
                query = it
                viewModel.getSearchItem(it)
            },
            active = isSearchBarActive,
            onActiveChange = {
                communicatorViewModel.onEvent(MainViewModel.SharedEvents.ToggleSearchActive)
                query = ""
                viewModel.getSearchItem(query)
            },
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
            },
            url = profileUrl,
            onProfileClick = {
                if (communicatorViewModel.checkHasLogIn()) {
                    isProfileDialogVisible = true
                } else {
                    communicatorViewModel.onEvent(
                        MainViewModel.SharedEvents.OpenLogInScreen
                    )
                }
            },
            contents = {
                AnimatedVisibility(isSearchBarActive) {
                    SearchScreen(
                        state = viewModel.homeSearchScreenState.value,
                        onSyllabusClick = { model ->
                            navigateToViewSyllabus(
                                navController,
                                homeScreenState.course,
                                homeScreenState.sem,
                                false,
                                model
                            )
                        },
                        onEventClick = { model ->
                            navigateToEvent(navController, model)
                        }
                    )
                }
            })
    }) {
        LazyColumn(
            modifier = Modifier
                .consumeWindowInsets(it)
                .animateContentSize(), contentPadding = it
        ) {
            singleElement(key = "Notification Permission") {
                if (!communicatorViewModel.isNotificationPrefItemVisible.value) {
                    PreferenceCard(modifier = Modifier.padding(
                        vertical = grid_1,
                        horizontal = grid_2
                    ),
                        title = "Notification is disabled",
                        icon = Icons.Outlined.NotificationsOff,
                        description = "Allow Notification to get latest notice and announcement",
                        endIcon = Icons.Outlined.Settings,
                        onClick = {
                            enableNotificationClick(
                                context
                            )
                        })
                }
            }
            singleElement(key = "Header") {
                HeaderCompose(
                    isEnable = isOnlineEnable,
                    onEnableChange = { currentState ->
                        viewModel.onEvent(
                            HomeScreenEvents.ToggleOnlineSyllabusClick(
                                currentState
                            )
                        )
                    },
                    onSettingClick = {
                        isChooseSemSelected = !isChooseSemSelected
                    },
                    isConnected = viewModel.isConnected
                        .collectAsState(initial = ConnectivityObserver.Status.Available)
                        .value,
                )
            }
            if (isOnlineEnable) onlineDataSource(
                onlineData.first,
                onlineData.second,
                onlineData.third,
                onClick = { model ->
                    navigateToViewSyllabus(
                        navController, homeScreenState.course, homeScreenState.sem, true, model
                    )
                })
            else offlineDataSource(
                theoryData = theory,
                labData = lab,
                peData = pe,
                onClick = { model ->
                    navigateToViewSyllabus(
                        navController, homeScreenState.course, homeScreenState.sem, false, model
                    )
                })
            showHoliday(holiday, endItem = "View All", endItemClick = {
                navController.navigate(
                    Screen.HolidayScreen.route
                )
            })
            showEvents(
                items = events,
                getAttach = viewModel.firebaseCase.getAttach,
                onClick = { event ->
                    navigateToEvent(navController, event)
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
            ChooseSemBottomSheet(model = courseDetails,
                sem = homeScreenState.sem,
                course = homeScreenState.course,
                onDismissRequest = {
                    isChooseSemSelected = false
                },
                onSaveClick = { it1 ->
                    viewModel.onEvent(
                        HomeScreenEvents.OnCourseChange(it1)
                    )
                })
        }
        if (isProfileDialogVisible) {
            ProfileDialog(
                viewModel = communicatorViewModel,
                onDismissRequest = {
                    isProfileDialogVisible = false
                }
            )
        }
        if (communicatorViewModel.isShowAlertDialog.value) {
            AppAlertDialog(model = communicatorViewModel.dialogModel.value, onDismissRequest = {
                communicatorViewModel.onDismissRequest()
            })
        }
    }
}

private fun navigateToEvent(
    navController: NavController,
    event: EventModel
) {
    navController.navigate(
        EventRoute.DetailScreen.route + "?eventId=${event.path}"
    )
}

private fun navigateToViewSyllabus(
    navController: NavController,
    course: String,
    sem: String,
    isOnlineEnable: Boolean,
    model: SyllabusUIModel
) {
    navController.navigate(
        CourseScreenRoute.ViewSubjectScreen.route +
                "?course=${(course).lowercase()}"
                + "&courseSem=${if (isOnlineEnable) "${course}${sem}".lowercase() else model.openCode}"
                + "&subject=${model.subject.replaceAmpersandWithAsterisk()}"
                + "&isOnline=$isOnlineEnable"
    )
}

@Composable
private fun HeaderCompose(
    modifier: Modifier = Modifier,
    isEnable: Boolean = false,
    isConnected: ConnectivityObserver.Status = ConnectivityObserver.Status.Available,
    onEnableChange: (Boolean) -> Unit = {},
    onEditClick: () -> Unit = {},
    onSettingClick: () -> Unit = {}
) {
    val context = LocalContext.current
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
        AnimatedVisibility(visible = isConnected != ConnectivityObserver.Status.Available) {
            ImageIconButton(
                icon = Icons.Outlined.WifiOff,
                tint = MaterialTheme.colorScheme.error,
                onClick = {
                    Toast.makeText(
                        context, context.getString(R.string.offline_mode_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
        AnimatedVisibility(visible = /*!isEnable*/ false) {
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

private fun enableNotificationClick(
    context: Context
) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", context.packageName, null)
    intent.data = uri
    context.startActivity(intent)
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    BITAppTheme {
        HeaderCompose()
    }
}