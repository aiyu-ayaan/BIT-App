/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.attendance.attendance_screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atech.bit.ui.comman.BottomPadding
import com.atech.bit.ui.comman.singleElement
import com.atech.bit.ui.navigation.AttendanceRoute
import com.atech.bit.ui.screens.attendance.attendance_screen.AttendanceEvent
import com.atech.bit.ui.screens.attendance.attendance_screen.AttendanceViewModel
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.ui.theme.grid_3
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColumnScope.bottomSheetAddFromSyllabus(
    modifier: Modifier = Modifier,
    viewModel: AttendanceViewModel = hiltViewModel(),
    navController: NavController = rememberNavController(),
    dismissRequest: () -> Unit
) = this.apply {

    val fetchOfflineSyllabus = viewModel.fetchSyllabus.value.second
    val fetchOnlineSyllabus = viewModel.fetchSyllabus.value.first
    val coroutineScope = rememberCoroutineScope()

    var selectTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    LaunchedEffect(key1 = true) {
        viewModel.getSubjectFromSyllabus()
    }

    var isTabVisible by rememberSaveable {
        mutableStateOf(true)
    }

    Column(
        modifier = modifier
    ) {
        TabRow(
            containerColor = BottomSheetDefaults.ContainerColor,
            selectedTabIndex = selectTabIndex,
        ) {
            if (isTabVisible)
                Tab(selected = selectTabIndex == 0, onClick = { selectTabIndex = 0 }) {
                    Text(
                        text = "From Online", Modifier.padding(grid_1)
                    )
                }
            Tab(selected = selectTabIndex == 1, onClick = { selectTabIndex = 1 }) {
                Text(
                    text = "From Offline", Modifier.padding(grid_1)
                )
            }
        }
        LazyColumn(
            modifier = Modifier,
        ) {
            if (fetchOfflineSyllabus.isEmpty() && fetchOnlineSyllabus.isEmpty()) {
                singleElement(key = "Progress") {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.padding(grid_3))
                        CircularProgressIndicator(
                            modifier = modifier.size(80.dp), strokeWidth = grid_1
                        )
                    }
                }
                return@LazyColumn
            }
            if (fetchOnlineSyllabus.size == 1 && fetchOnlineSyllabus[0].subject.isBlank()) {
                selectTabIndex = 1
                isTabVisible = false
            }
            if (selectTabIndex == 1) {
                items(items = fetchOfflineSyllabus, key = { model ->
                    model.subject + model.isFromOnline
                }) { model ->
                    AttendanceSyllabusItem(
                        model = model,
                        onClick = { clickItem, isChecked ->
                            viewModel.onEvent(
                                AttendanceEvent.AddFromSyllabusItemClick(
                                    model = clickItem, isAdded = isChecked
                                )
                            )
                        },
                        onEditClick = { model1 ->
                            coroutineScope.launch {
                                viewModel.getElementIdFromSubject(
                                    model1.subject
                                )?.let {
                                    navigateToEdit(
                                        navController,
                                        it
                                    )
                                }
                                dismissRequest.invoke()
                            }
                        }
                    )
                }
            } else {
                items(items = fetchOnlineSyllabus, key = { model ->
                    model.subject + model.isFromOnline
                }) { model ->
                    AttendanceSyllabusItem(model = model,
                        isOnline = true,
                        onClick = { clickItem, isChecked ->
                            viewModel.onEvent(
                                AttendanceEvent.AddFromSyllabusItemClick(
                                    model = clickItem, isAdded = isChecked
                                )
                            )
                        },
                        onEditClick = { model1 ->
                            coroutineScope.launch {
                                viewModel.getElementIdFromSubject(
                                    model1.subject
                                )?.let {
                                    navigateToEdit(
                                        navController,
                                        it
                                    )
                                }
                                dismissRequest.invoke()
                            }
                        }
                    )
                }
            }
        }
        BottomPadding()
        BottomPadding()
    }
}

fun navigateToEdit(
    navController: NavController, id: Int
) {
    navController.navigate(
        AttendanceRoute.AddEditAttendanceScreen.route
                + "?attendanceId=${id}"
                + "&fromAddFromSyllabus=${1}"
    )
}

@Preview(showBackground = true)
@Composable
fun BottomSheetAddFromSyllabusPreview() {
    BITAppTheme {
//        BottomSheetAddFromSyllabus()
    }
}