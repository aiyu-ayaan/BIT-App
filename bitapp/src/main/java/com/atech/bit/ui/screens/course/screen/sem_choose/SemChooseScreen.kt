/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.course.screen.sem_choose

import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.atech.bit.ui.comman.BackToolbar
import com.atech.bit.ui.comman.BottomPadding
import com.atech.bit.ui.comman.NetworkScreenEmptyScreen
import com.atech.bit.ui.comman.singleElement
import com.atech.bit.ui.navigation.CourseScreenRoute
import com.atech.bit.ui.navigation.encodeUrl
import com.atech.bit.ui.navigation.replaceAmpersandWithAsterisk
import com.atech.bit.ui.screens.course.CourseEvents
import com.atech.bit.ui.screens.course.CourseViewModel
import com.atech.bit.ui.screens.course.components.SubjectItem
import com.atech.bit.ui.screens.course.components.SubjectTitle
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.ui.theme.grid_2
import com.atech.bit.ui.theme.grid_3
import com.atech.core.datasource.firebase.remote.model.CourseDetailModel
import com.atech.core.datasource.room.syllabus.SubjectType
import com.atech.core.usecase.SyllabusUIModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(
    ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class
)
@Composable
fun SemChooseScreen(
    modifier: Modifier = Modifier,
    viewModel: CourseViewModel = hiltViewModel(),
    navController: NavController = rememberNavController()
) {
    val scrollState = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackBarState = remember { SnackbarHostState() }

    val enable = viewModel.isSelected.value
    val courseModel by viewModel.currentClickItem
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(viewModel.currentSem.value - 1) }
    val theoryData = viewModel.theory.collectAsLazyPagingItems()
    val labData = viewModel.lab.collectAsLazyPagingItems()
    val peData = viewModel.pe.collectAsLazyPagingItems()
    var isEmptyScreenVisible = remember { mutableStateOf(false) }

    val onlineData = viewModel.onlineSyllabus.value

    LaunchedEffect(key1 = true) {
        viewModel.oneTimeEvent.collectLatest { event ->
            when (event) {
                is CourseViewModel.OneTimeEvent.ShowSnackBar ->
                    snackBarState.showSnackbar(
                        message = event.message
                    )
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackBarState)
        },
        topBar = {
            ToolbarCompose(
                courseModel, enable, navController, onCheckedChange = {
                    viewModel.onEvent(
                        CourseEvents.OnSwitchToggle
                    )
                }, scrollState
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .consumeWindowInsets(it)
                .nestedScroll(scrollState.nestedScrollConnection), contentPadding = it
        ) {
            singleElement(key = "Tab") {
                TabBarSem(courseModel = courseModel,
                    selectedTabIndex = selectedTabIndex,
                    onClick = { tabIndex ->
                        selectedTabIndex = tabIndex
                        viewModel.onEvent(
                            CourseEvents.OnSemChange(tabIndex + 1)
                        )
                    })
            }
            if (enable) onlineDataSource(
                onlineData.first,
                onlineData.second,
                onlineData.third,
                onClick = { model ->
                    navigateToViewSubjectScreen(
                        navController, viewModel, model
                    )
                })
            else {
                if (theoryData.itemCount == 0 && labData.itemCount == 0 && peData.itemCount == 0)
                    isEmptyScreenVisible.value = true
                else {
                    isEmptyScreenVisible.value = false
                    offlineDataSource(theoryData, labData, peData, onClick = { model ->
                        navigateToViewSubjectScreen(navController, viewModel, model, false)
                    })
                }
            }
            if (isEmptyScreenVisible.value) {
                singleElement(key = "Empty Screen") {
                    Spacer(modifier = Modifier.height(grid_3))
                    ShowEmptyScreen()
                }
            }

            singleElement(key = "BottomPadding") { BottomPadding() }
        }
    }
}

@Composable
private fun ShowEmptyScreen() {
    NetworkScreenEmptyScreen(
        modifier = Modifier.fillMaxSize(),
        text = "No syllabus found for this semester."
    )
}

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.onlineDataSource(
    theory: List<SyllabusUIModel>,
    lab: List<SyllabusUIModel>,
    pe: List<SyllabusUIModel>,
    onClick: (SyllabusUIModel) -> Unit = {}
) {

    if (theory.isNotEmpty()) {
        singleElement(
            key = SubjectType.THEORY.name + "Online"
        ) { SubjectTitle("Theory") }
        items(items = theory, key = { item -> item.subject + item.code + "online" }) { ele ->
            SubjectItem(
                data = ele, modifier = Modifier.animateItemPlacement(
                    animationSpec = spring(
                        dampingRatio = 2f, stiffness = 600f
                    )
                ), onClick = onClick
            )
        }
    }
    if (lab.isNotEmpty()) {
        singleElement(
            key = SubjectType.LAB.name + "Online"
        ) { SubjectTitle("Lab") }
        items(items = lab, key = { item -> item.subject + item.code }) { ele ->
            SubjectItem(
                data = ele, modifier = Modifier.animateItemPlacement(
                    animationSpec = spring(
                        dampingRatio = 2f, stiffness = 600f
                    )
                ), onClick = onClick
            )
        }
    }
    if (pe.isNotEmpty()) {
        singleElement(
            key = SubjectType.PE.name + "Online"
        ) { SubjectTitle("PE") }
        items(items = pe, key = { item -> item.subject + item.code }) { ele ->
            SubjectItem(
                data = ele, modifier = Modifier.animateItemPlacement(
                    animationSpec = spring(
                        dampingRatio = 2f, stiffness = 600f
                    )
                ), onClick = onClick
            )
        }
    }

}


@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.offlineDataSource(
    theoryData: LazyPagingItems<SyllabusUIModel>,
    labData: LazyPagingItems<SyllabusUIModel>,
    peData: LazyPagingItems<SyllabusUIModel>,
    onClick: (SyllabusUIModel) -> Unit = {}
) {
    if (theoryData.itemCount > 0) {
        singleElement(
            key = SubjectType.THEORY.name
        ) { SubjectTitle("Theory") }
        items(count = theoryData.itemCount,
            key = theoryData.itemKey { model -> model.openCode + "offline" },
            contentType = theoryData.itemContentType { "Theory" }) { index ->
            theoryData[index]?.let { model ->
                SubjectItem(
                    data = model, modifier = Modifier.animateItemPlacement(
                        animationSpec = spring(
                            dampingRatio = 2f, stiffness = 600f
                        )
                    ), onClick = onClick
                )
            }
        }
    }
    if (labData.itemCount > 0) {
        singleElement(key = SubjectType.LAB.name) { SubjectTitle("Lab") }
        items(count = labData.itemCount,
            key = labData.itemKey { link -> link.openCode },
            contentType = labData.itemContentType { "Lab" }) { index ->
            labData[index]?.let { model ->
                SubjectItem(
                    data = model, modifier = Modifier.animateItemPlacement(
                        animationSpec = spring(
                            dampingRatio = 2f, stiffness = 600f
                        )
                    ), onClick = onClick
                )
            }
        }
    }
    if (peData.itemCount > 0) {
        singleElement(key = SubjectType.PE.name) { SubjectTitle("PE") }
        items(count = peData.itemCount,
            key = peData.itemKey { link -> link.openCode },
            contentType = peData.itemContentType { "PE" }) { index ->
            peData[index]?.let { model ->
                SubjectItem(
                    data = model, modifier = Modifier.animateItemPlacement(
                        animationSpec = spring(
                            dampingRatio = 2f, stiffness = 600f
                        )
                    ), onClick = onClick
                )
            }
        }
    }

}

private fun navigateToViewSubjectScreen(
    navController: NavController,
    viewModel: CourseViewModel,
    model: SyllabusUIModel,
    isOnline: Boolean = true
) {

    navController.navigate(
        CourseScreenRoute.ViewSubjectScreen.route
                + "?course=${viewModel.currentClickItem.value.name}"
                + "&courseSem=${if (isOnline) viewModel.currentClickItem.value.name + viewModel.currentSem.value else model.openCode}"
                + "&subject=${model.subject.replaceAmpersandWithAsterisk()}"
                + "&isOnline=$isOnline"
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToolbarCompose(
    courseModel: CourseDetailModel,
    enable: Boolean,
    navController: NavController,
    onCheckedChange: (Boolean) -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior
) {
    BackToolbar(scrollBehavior = scrollBehavior,
        title = courseModel.name.replaceFirstChar { it.uppercase() },
        actions = {
            Switch(colors = SwitchDefaults.colors(
                checkedIconColor = MaterialTheme.colorScheme.primary
            ), checked = enable, onCheckedChange = {
                onCheckedChange.invoke(it)
            }, thumbContent = {
                Icon(
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                    imageVector = if (enable) Icons.Outlined.Wifi
                    else Icons.Outlined.WifiOff, contentDescription = null
                )
            })
        },
        onNavigationClick = {
            navController.navigateUp()
        })
}

@Composable
fun TabBarSem(
    modifier: Modifier = Modifier,
    courseModel: CourseDetailModel,
    selectedTabIndex: Int,
    onClick: (Int) -> Unit = {}
) {
    val semList = (1..courseModel.sem).toList().map { "Semester $it" }
    ScrollableTabRow(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Red),
        edgePadding = grid_2,
        selectedTabIndex = selectedTabIndex,

        ) {
        semList.forEachIndexed { index, s ->
            Tab(selected = index == selectedTabIndex, onClick = {
                onClick.invoke(index)
            }) {
                Text(
                    text = s, maxLines = 2, modifier = Modifier.padding(grid_1)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SemChooseScreenPreview() {
    BITAppTheme {}
}