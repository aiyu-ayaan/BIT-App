package com.atech.course.screen.sem_choose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atech.components.BackToolbar
import com.atech.components.BottomPadding
import com.atech.components.singleElement
import com.atech.core.firebase.remote.model.CourseDetailModel
import com.atech.course.CourseViewModel
import com.atech.theme.BITAppTheme
import com.atech.theme.grid_1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemChooseScreen(
    modifier: Modifier = Modifier,
    viewModel: CourseViewModel = hiltViewModel(),
    navController: NavController = rememberNavController()
) {
    var enable by remember {
        mutableStateOf(true)
    }
    val courseModel by viewModel.currentClickItem
    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                BackToolbar(
                    title = courseModel.name.replaceFirstChar { it.uppercase() },
                    actions = {
                        Switch(
                            colors = SwitchDefaults.colors(
                                checkedIconColor = MaterialTheme.colorScheme.primary
                            ),
                            checked = enable,
                            onCheckedChange = {
                                enable = it
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
                TabBarSem(courseModel = courseModel)
            }
        },
    ) {
        LazyColumn(
            modifier = Modifier.padding(it),
            contentPadding = it
        ) {
            singleElement { BottomPadding() }
        }
    }
}

@Composable
fun TabBarSem(
    modifier: Modifier = Modifier,
    courseModel: CourseDetailModel
) {
    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    val semList = (1..courseModel.sem).toList()
        .map { "Semester $it" }
    ScrollableTabRow(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Red),
        selectedTabIndex = selectedTabIndex,

        ) {
        semList.forEachIndexed { index, s ->
            Tab(
                selected = index == selectedTabIndex,
                onClick = {
                    selectedTabIndex = index
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
        TabBarSem(
            courseModel = CourseDetailModel("BCA", 6)
        )
    }
}