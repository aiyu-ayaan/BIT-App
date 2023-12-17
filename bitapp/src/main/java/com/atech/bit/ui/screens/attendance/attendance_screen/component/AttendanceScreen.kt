package com.atech.bit.ui.screens.attendance.attendance_screen.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.atech.bit.R
import com.atech.bit.ui.comman.BottomPadding
import com.atech.bit.ui.comman.EmptyScreen
import com.atech.bit.ui.comman.ImageIconButton
import com.atech.bit.ui.comman.ImageIconModel
import com.atech.bit.ui.comman.singleElement
import com.atech.bit.ui.navigation.AttendanceRoute
import com.atech.bit.ui.screens.attendance.attendance_screen.AttendanceEvent
import com.atech.bit.ui.screens.attendance.attendance_screen.AttendanceViewModel
import com.atech.bit.ui.theme.captionColor
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.ui.theme.grid_2
import com.atech.core.datasource.room.attendance.AttendanceModel
import kotlinx.coroutines.flow.collectLatest


@OptIn(
    ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
fun AttendanceScreen(
    modifier: Modifier = Modifier,
    viewModel: AttendanceViewModel = hiltViewModel(),
    navController: NavController = rememberNavController()
) {
    val attendanceList = viewModel.attendance.collectAsLazyPagingItems()
    val lazyListState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val selectedAttendance = viewModel.selectedAttendance.value
    var currentClickAttendance by rememberSaveable {
        mutableStateOf<AttendanceModel?>(null)
    }
    val snackBarHostState = remember {
        SnackbarHostState()
    }
    val attendanceListSize = rememberSaveable {
        mutableIntStateOf(0)
    }
    var isSelectWindowActive: Boolean by rememberSaveable {
        mutableStateOf(false)
    }
    var isSelectAllClick by rememberSaveable {
        mutableStateOf(false)
    }
    var isDialogBoxVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var isAddFromSyllabusBottomSheetVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var isArchiveBottomSheetVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var isSettingDialogVisible by rememberSaveable {
        mutableStateOf(false)
    }
    val defPercentage = viewModel.defaultPercentage.value
    BackHandler {
        if (isSelectWindowActive) {
            isSelectWindowActive = false
            viewModel.onEvent(AttendanceEvent.ClearSelection)
        } else {
            navController.navigateUp()
        }
    }
    LaunchedEffect(key1 = true) {
        viewModel.oneTimeAttendanceScreenEvent.collectLatest { event ->
            when (event) {
                is AttendanceViewModel.OneTimeAttendanceEvent.ShowUndoDeleteAttendanceMessage ->
                    snackBarHostState.showSnackbar(
                        event.message,
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    ).let { snackBarResult ->
                        if (snackBarResult == SnackbarResult.ActionPerformed) viewModel.onEvent(
                            AttendanceEvent.RestorerAttendance
                        )
                    }
            }
        }
    }

    Scaffold(modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            AnimatedVisibility(
                visible = attendanceList.itemCount != 0,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                AttendanceTopBar(
                    scrollBehavior = scrollBehavior,
                    defPercentage = defPercentage
                )
            }
        },
        bottomBar = {
            AttendanceBottomAppbar(
                isSelectWindowActive = isSelectWindowActive,
                action = {
                    navController.navigate(
                        AttendanceRoute.AddEditAttendanceScreen.route
                    )
                },
                onSelectClick = {
                    isSelectWindowActive = !isSelectWindowActive
                    if (!isSelectWindowActive) {
                        viewModel.onEvent(AttendanceEvent.ClearSelection)
                        isSelectAllClick = false
                    }
                },
                onDeleteClick = {
                    isDialogBoxVisible = true
                },
                list = selectedAttendance,
                checkBoxTickState = selectedAttendance.size == attendanceListSize.intValue,
                onCheckBoxClick = {
                    isSelectAllClick = !isSelectAllClick
                    viewModel
                        .onEvent(
                            AttendanceEvent.SelectAllClick(
                                attendanceList.itemSnapshotList.items.toList(),
                                isSelectAllClick
                            )
                        )
                },
                onArchiveClick = {
                    if (isSelectWindowActive)
                        viewModel.onEvent(AttendanceEvent.SelectedItemToArchive)
                    else
                        isArchiveBottomSheetVisible = true
                },
                onAddFromSyllabusClick = {
                    isAddFromSyllabusBottomSheetVisible = !isAddFromSyllabusBottomSheetVisible
                },
                onSettingClick = {
                    isSettingDialogVisible = true
                }
            )
        })
    {
        if (isAddFromSyllabusBottomSheetVisible)
            ModalBottomSheet(
                onDismissRequest = { isAddFromSyllabusBottomSheetVisible = false }
            ) {
                bottomSheetAddFromSyllabus(
                    viewModel = viewModel,
                    navController = navController,
                    dismissRequest = {
                        isAddFromSyllabusBottomSheetVisible = false
                    }
                )
            }
        if (isArchiveBottomSheetVisible) {
            ModalBottomSheet(onDismissRequest = { isArchiveBottomSheetVisible = false }) {
                bottomSheetArchive(
                    viewModel = viewModel
                )
            }
        }
        if (isSettingDialogVisible) {
            Dialog(
                onDismissRequest = { isSettingDialogVisible = false },
            ) {
                AttendanceSettingDialog(
                    viewModel = viewModel,
                    onDismiss = {
                        isSettingDialogVisible = false
                    }
                )
            }
        }
        if (attendanceList.itemCount == 0) {
            EmptyScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            )
            return@Scaffold
        }
        if (isDialogBoxVisible) {
            ShowWarningDialog(
                onDismissRequest = {
                    isDialogBoxVisible = false
                },
                onConfirmClick = {
                    viewModel.onEvent(
                        AttendanceEvent.DeleteSelectedItems
                    )
                    isDialogBoxVisible = false
                },
                onDismissClick = {
                    isDialogBoxVisible = false
                }
            )
        }
        val sheetState = rememberModalBottomSheetState()
        var isCalenderBottomSheetVisible by rememberSaveable {
            mutableStateOf(false)
        }

        if (isCalenderBottomSheetVisible && currentClickAttendance != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    isCalenderBottomSheetVisible = false
                }, sheetState = sheetState
            ) {
                AttendanceCalenderView(
                    model = currentClickAttendance!!
                )
            }
        }

        var isMenuBottomSheetVisible by rememberSaveable {
            mutableStateOf(false)
        }
        if (isMenuBottomSheetVisible && currentClickAttendance != null) {
            ModalBottomSheet(onDismissRequest = { isMenuBottomSheetVisible = false }) {
                AttendanceMenu(attendanceModel = currentClickAttendance!!,
                    onEditClick = { attendanceModel ->
                        navController.navigate(
                            AttendanceRoute.AddEditAttendanceScreen.route +
                                    "?attendanceId=${attendanceModel.id}"
                        )
                    },
                    isUndoEnable = currentClickAttendance?.stack?.isNotEmpty() ?: true,
                    onUndoClick = { attendanceModel ->
                        viewModel.onEvent(
                            AttendanceEvent.UndoAttendanceState(attendanceModel)
                        )
                    },
                    onArchiveClick = { attendanceModel ->
                        viewModel.onEvent(
                            AttendanceEvent.ArchiveAttendance(attendanceModel)
                        )
                    },
                    onDeleteClick = { attendanceModel ->
                        viewModel.onEvent(
                            AttendanceEvent.DeleteAttendance(
                                attendanceModel
                            )
                        )
                    },
                    commonAction = {
                        isMenuBottomSheetVisible = false
                    })
            }
        }
        LazyColumn(
            modifier = Modifier
                .consumeWindowInsets(it)
                .animateContentSize(),
            contentPadding = it,
            state = lazyListState
        ) {
            attendanceListSize.intValue = attendanceList.itemCount
            items(count = attendanceList.itemCount,
                key = attendanceList.itemKey { model -> model.id },
                contentType = attendanceList.itemContentType { it1 -> it1.subject }) { index ->
                attendanceList[index]?.let { model ->
                    AttendanceItem(
                        model = model,
                        minPercentage = defPercentage,
                        modifier = Modifier.animateItemPlacement(
                            animationSpec = spring(
                                dampingRatio = 2f, stiffness = 600f
                            )
                        ),
                        onTickOrCrossClickClick = { clickItems, isPresent ->
                            viewModel.onEvent(
                                AttendanceEvent.ChangeAttendanceValue(
                                    attendanceModel = clickItems, isPresent = isPresent
                                )
                            )
                        },
                        onClick = { it1 ->
                            currentClickAttendance = it1
                            isCalenderBottomSheetVisible = true
                        },
                        onLongClick = { it1 ->
                            currentClickAttendance = it1
                            isMenuBottomSheetVisible = true
                        },
                        isCheckBoxVisible = isSelectWindowActive,
                        onSelect = { clAtt, isSelected ->
                            viewModel.onEvent(
                                AttendanceEvent.ItemSelectedClick(
                                    attendanceModel = clAtt,
                                    isAdded = isSelected
                                )
                            )
                        },
                        isItemIsSelected = selectedAttendance.contains(model)
                    )
                }
            }
            singleElement(key = "Bottom Padding") {
                BottomPadding()
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AttendanceTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    defPercentage: Int
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = "Goal : $defPercentage%",
                    modifier = Modifier.padding(start = grid_1),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(grid_1))
                Text(
                    text = "😎",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(grid_1))
                Text(
                    text = "Current : 75%",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(end = grid_1),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }, scrollBehavior = scrollBehavior
    )
}

@Composable
fun ShowWarningDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onDismissClick: () -> Unit = {}
) {
    AlertDialog(
        modifier = modifier,
        icon = {
            Icon(imageVector = Icons.Outlined.DeleteForever, contentDescription = null)
        },
        title = {
            Text(text = "Delete Attendance")
        },
        text = {
            Text(text = "Are you sure you want to delete selected attendance?")
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirmClick) {
                Icon(
                    modifier = Modifier.padding(end = grid_1),
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null
                )
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissClick) {
                Icon(
                    modifier = Modifier.padding(end = grid_1),
                    imageVector = Icons.Default.Cancel,
                    contentDescription = null
                )
                Text(text = "Cancel")
            }
        }
    )
}

@Composable
fun AttendanceBottomAppbar(
    modifier: Modifier = Modifier,
    action: () -> Unit = {},
    isSelectWindowActive: Boolean = false,
    onAddFromSyllabusClick: () -> Unit = {},
    onArchiveClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
    list: List<AttendanceModel> = emptyList(),
    checkBoxTickState: Boolean = false,
    onCheckBoxClick: () -> Unit = {},
) {
    val actionList = listOf(
        ImageIconModel(
            imageVector = Icons.Outlined.Book,
            contentDescription = R.string.add_from_Syllabus,
            onClick = onAddFromSyllabusClick,
            isVisible = !isSelectWindowActive
        ),
        ImageIconModel(
            imageVector = Icons.Outlined.Archive,
            contentDescription = R.string.archive,
            onClick = onArchiveClick,
            isEnable = if (isSelectWindowActive) list.isNotEmpty() else true,
            tint = if (isSelectWindowActive)
                if (list.isEmpty())
                    MaterialTheme.colorScheme.captionColor
                else
                    MaterialTheme.colorScheme.primary
            else
                LocalContentColor.current

        ),
        ImageIconModel(
            imageVector = Icons.Outlined.Settings,
            contentDescription = R.string.settings,
            onClick = onSettingClick,
            isVisible = !isSelectWindowActive
        ),
        ImageIconModel(
            imageVector = Icons.Outlined.Delete,
            contentDescription = R.string.delete,
            onClick = onDeleteClick,
            isVisible = isSelectWindowActive,
            isEnable = if (isSelectWindowActive) list.isNotEmpty() else true,
            tint = if (isSelectWindowActive)
                if (list.isEmpty())
                    MaterialTheme.colorScheme.captionColor
                else
                    MaterialTheme.colorScheme.primary
            else
                LocalContentColor.current
        ),
        ImageIconModel(
            imageVector = Icons.Outlined.Checklist,
            contentDescription = R.string.select,
            onClick = onSelectClick,
            tint = if (isSelectWindowActive) MaterialTheme.colorScheme.primary
            else LocalContentColor.current
        ),
    )
    BottomAppBar(modifier = modifier, actions = {
        AnimatedVisibility(visible = isSelectWindowActive) {
            TriStateCheckbox(
                state = when {
                    list.isEmpty() -> ToggleableState.Off
                    checkBoxTickState -> ToggleableState.On
                    else -> ToggleableState.Indeterminate
                },
                onClick = {
                    onCheckBoxClick()
                }
            )
        }
        AnimatedVisibility(visible = isSelectWindowActive) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = grid_2),
                text = stringResource(id = R.string.count_selected, list.size.toString()),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        actionList.forEach { action ->
            AnimatedVisibility(visible = action.isVisible) {
                ImageIconButton(iconModel = action)
            }
        }
    }, floatingActionButton = {
        AnimatedVisibility(
            visible = !isSelectWindowActive,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            FloatingActionButton(
                onClick = action,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add, contentDescription = "add"
                )
            }
        }
    })
}
