package com.atech.attendance.screen.attendance.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.atech.attendance.screen.attendance.AttendanceViewModel
import com.atech.components.BottomPadding
import com.atech.theme.BITAppTheme
import com.atech.theme.grid_1

@Composable
fun ColumnScope.bottomSheetAddFromSyllabus(
    modifier: Modifier = Modifier,
    viewModel: AttendanceViewModel = hiltViewModel()
) = this.apply {

    LaunchedEffect(key1 = true) {
        viewModel.getSubjectFromSyllabus()
    }

    val fetchSyllabus = viewModel.fetchSyllabus.value

    var selectTabIndex by rememberSaveable {
        mutableIntStateOf(1)
    }
    Column(
        modifier = modifier
    ) {
        TabRow(
            selectedTabIndex = selectTabIndex,
        ) {
            Tab(
                selected = selectTabIndex == 0,
                onClick = { selectTabIndex = 0 }
            ) {
                Text(
                    text = "From Online",
                    Modifier.padding(grid_1)
                )
            }
            Tab(
                selected = selectTabIndex == 1,
                onClick = { selectTabIndex = 1 }
            ) {
                Text(
                    text = "From Offline",
                    Modifier.padding(grid_1)
                )
            }
        }
        LazyColumn(
            modifier = Modifier
        ) {
            items(
                items = fetchSyllabus,
                key = { model ->
                    model.subject + model.isFromOnline
                }
            ) { model ->
                AttendanceSyllabusItem(model = model)
            }
        }
        BottomPadding()
        BottomPadding()
    }
}

@Preview(showBackground = true)
@Composable
fun BottomSheetAddFromSyllabusPreview() {
    BITAppTheme {
//        BottomSheetAddFromSyllabus()
    }
}