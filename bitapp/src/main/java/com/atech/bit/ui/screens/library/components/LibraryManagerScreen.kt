package com.atech.bit.ui.screens.library.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.atech.bit.R
import com.atech.bit.ui.comman.BackToolbar
import com.atech.bit.ui.comman.ImageIconButton
import com.atech.bit.ui.navigation.LibraryRoute
import com.atech.bit.ui.screens.library.LibraryEvent
import com.atech.bit.ui.screens.library.LibraryManagerViewModel
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.captionColor
import com.atech.bit.ui.theme.grid_1
import com.atech.core.utils.CalendarReminder
import kotlinx.coroutines.flow.collectLatest

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalFoundationApi::class
)
@Composable
fun LibraryManagerScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController(),
    viewModel: LibraryManagerViewModel = hiltViewModel()
) {
    val items = viewModel.libraryList.value
    val snackBarState = remember {
        SnackbarHostState()
    }
    var isDeleteAllDialogVisible by rememberSaveable {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.oneTimeEvent.collectLatest { event ->
            when (event) {
                is LibraryManagerViewModel.LibraryOneTimeEvent.ShowActionSnackBar -> {
                    snackBarState.showSnackbar(
                        event.message, duration = SnackbarDuration.Long, actionLabel = "Undo"
                    ).let {
                        if (it == SnackbarResult.ActionPerformed) {
                            viewModel.onEvent(LibraryEvent.UndoDelete)
                        }
                    }
                }

                LibraryManagerViewModel.LibraryOneTimeEvent.ShowSnackBar -> snackBarState.showSnackbar(
                    "Item updated !!", duration = SnackbarDuration.Short
                )
            }
        }
    }
    Scaffold(modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackBarState) },
        topBar = {
            BackToolbar(title = R.string.library, onNavigationClick = {
                navHostController.navigateUp()
            })
        },
        bottomBar = {
            BottomAppBar(actions = {
                ImageIconButton(
                    icon = Icons.Outlined.DeleteSweep,
                    isEnable = items.isNotEmpty(),
                    onClick = {
                        isDeleteAllDialogVisible = !isDeleteAllDialogVisible
                    },
                    tint = if (items.isNotEmpty()) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.captionColor
                )
            }, floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        viewModel.onEvent(LibraryEvent.NavigateToAddEditScreen())
                        navHostController.navigate(
                            LibraryRoute.LibraryAddEditScreen.route
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(imageVector = Icons.Outlined.Edit, contentDescription = null)
                }
            })
        }) {
        LazyColumn(
            modifier = Modifier.consumeWindowInsets(it), contentPadding = it
        ) {
            items(items, key = { it1 -> it1.id }) { model ->
                LibraryItem(modifier = Modifier.animateItemPlacement(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ), model = model, onEditClick = {
                    viewModel.onEvent(LibraryEvent.NavigateToAddEditScreen(model))
                    navHostController.navigate(
                        LibraryRoute.LibraryAddEditScreen.route
                    )
                }, onTickClick = { item ->
                    viewModel.onEvent(
                        LibraryEvent.OnTickClick(
                            item
                        )
                    )
                    if (item.eventId != -1L) {
                        CalendarReminder.deleteEvent(
                            context = context, eventID = item.eventId
                        )
                    }
                }, onDeleteClick = { item ->
                    viewModel.onEvent(
                        LibraryEvent.OnDeleteClick(
                            item
                        )
                    )
                })
            }
        }

        if (isDeleteAllDialogVisible) {
            AlertDialog(
                icon = {
                    Icon(imageVector = Icons.Outlined.DeleteForever, contentDescription = null)
                },
                title = {
                    Text(text = "Delete Listed Books")
                },
                text = {
                    Text(text = "Are you sure you want to delete all items?")
                },
                onDismissRequest = { isDeleteAllDialogVisible = false },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.onEvent(
                            LibraryEvent.DeleteAll
                        )
                        isDeleteAllDialogVisible = false
                    }) {
                        Icon(
                            modifier = Modifier.padding(end = grid_1),
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null
                        )
                        Text(text = "Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isDeleteAllDialogVisible = false }) {
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
    }
}

@Preview(showBackground = true)
@Composable
fun LibraryManagerScreenPreview() {
    BITAppTheme {
        LibraryManagerScreen()
    }
}