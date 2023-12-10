package com.atech.course.screen.sem_choose

import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
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
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.atech.components.BackToolbar
import com.atech.components.BottomPadding
import com.atech.components.singleElement
import com.atech.core.firebase.remote.model.CourseDetailModel
import com.atech.core.room.syllabus.SubjectType
import com.atech.course.CourseEvents
import com.atech.course.CourseViewModel
import com.atech.course.components.SubjectItem
import com.atech.course.components.SubjectTitle
import com.atech.theme.BITAppTheme
import com.atech.theme.grid_1
import com.atech.theme.grid_2

@OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class
)
@Composable
fun SemChooseScreen(
    modifier: Modifier = Modifier,
    viewModel: CourseViewModel = hiltViewModel(),
    navController: NavController = rememberNavController()
) {
    val scrollState = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var enable by remember {
        mutableStateOf(true)
    }
    val courseModel by viewModel.currentClickItem
    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(viewModel.currentSem.value - 1)
    }
    val theoryData = viewModel.theory.collectAsLazyPagingItems()
    val labData = viewModel.lab.collectAsLazyPagingItems()
    val peData = viewModel.pe.collectAsLazyPagingItems()
    Scaffold(
        modifier = modifier,
        topBar = {
            ToolbarCompose(
                courseModel, enable, navController,
                onCheckedChange = {
                    enable = it
                },
                scrollState
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .consumeWindowInsets(it)
                .nestedScroll(scrollState.nestedScrollConnection),
            contentPadding = it
        ) {
            singleElement(key = "Tab") {
                TabBarSem(
                    courseModel = courseModel,
                    selectedTabIndex = selectedTabIndex,
                    onClick = { tabIndex ->
                        selectedTabIndex = tabIndex
                        viewModel.onEvent(
                            CourseEvents.OnSemChange(tabIndex + 1)
                        )
                    }
                )
            }
            if (theoryData.itemCount > 0) {
                singleElement(
                    key = SubjectType.THEORY.name
                )
                { SubjectTitle("Theory") }
                items(
                    count = theoryData.itemCount,
                    key = theoryData.itemKey { model -> model.openCode },
                    contentType = theoryData.itemContentType { "Theory" }
                ) { index ->
                    theoryData[index]?.let { model ->
                        SubjectItem(
                            data = model,
                            modifier = Modifier.animateItemPlacement(
                                animationSpec = spring(
                                    dampingRatio = 2f, stiffness = 600f
                                )
                            )
                        )
                    }
                }
            }
            if (labData.itemCount > 0) {
                singleElement(key = SubjectType.LAB.name) { SubjectTitle("Lab") }
                items(
                    count = labData.itemCount,
                    key = labData.itemKey { link -> link.openCode },
                    contentType = labData.itemContentType { "Lab" }
                )
                { index ->
                    labData[index]?.let { model ->
                        SubjectItem(
                            data = model,
                            modifier = Modifier.animateItemPlacement(
                                animationSpec = spring(
                                    dampingRatio = 2f, stiffness = 600f
                                )
                            )
                        )
                    }
                }
            }
            if (peData.itemCount > 0) {
                singleElement(key = SubjectType.PE.name) { SubjectTitle("PE") }
                items(
                    count = peData.itemCount,
                    key = peData.itemKey { link -> link.openCode },
                    contentType = peData.itemContentType { "PE" }
                )
                { index ->
                    peData[index]?.let { model ->
                        SubjectItem(
                            data = model,
                            modifier = Modifier.animateItemPlacement(
                                animationSpec = spring(
                                    dampingRatio = 2f, stiffness = 600f
                                )
                            )
                        )
                    }
                }
            }
            singleElement(key = "BottomPadding") { BottomPadding() }
        }
    }
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
    BackToolbar(
        scrollBehavior = scrollBehavior,
        title = courseModel.name.replaceFirstChar { it.uppercase() },
        actions = {
            Switch(
                colors = SwitchDefaults.colors(
                    checkedIconColor = MaterialTheme.colorScheme.primary
                ),
                checked = enable,
                onCheckedChange = {
                    onCheckedChange.invoke(it)
                },
                thumbContent = {
                    Icon(
                        imageVector = if (enable) Icons.Outlined.Wifi
                        else Icons.Outlined.WifiOff,
                        contentDescription = null
                    )
                }
            )
        },
        onNavigationClick = {
            navController.navigateUp()
        }
    )
}

@Composable
fun TabBarSem(
    modifier: Modifier = Modifier,
    courseModel: CourseDetailModel,
    selectedTabIndex: Int,
    onClick: (Int) -> Unit = {}
) {
    val semList = (1..courseModel.sem).toList()
        .map { "Semester $it" }
    ScrollableTabRow(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Red),
        edgePadding = grid_2,
        selectedTabIndex = selectedTabIndex,

        ) {
        semList.forEachIndexed { index, s ->
            Tab(
                selected = index == selectedTabIndex,
                onClick = {
                    onClick.invoke(index)
                }
            ) {
                Text(
                    text = s,
                    maxLines = 2,
                    modifier = Modifier.padding(grid_1)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SemChooseScreenPreview() {
    BITAppTheme {
    }
}