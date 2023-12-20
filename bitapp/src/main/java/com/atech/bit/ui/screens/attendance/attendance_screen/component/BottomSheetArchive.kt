package com.atech.bit.ui.screens.attendance.attendance_screen.component

import android.util.Log
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Unarchive
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import com.atech.bit.R
import com.atech.bit.ui.comman.BottomPadding
import com.atech.bit.ui.comman.ImageIconButton
import com.atech.bit.ui.comman.ImageIconModel
import com.atech.bit.ui.screens.attendance.attendance_screen.AttendanceEvent
import com.atech.bit.ui.screens.attendance.attendance_screen.AttendanceViewModel
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.captionColor
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.ui.theme.grid_2

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColumnScope.bottomSheetArchive(
    modifier: Modifier = Modifier,
    viewModel: AttendanceViewModel
) = this.apply {
    val archiveSubject = viewModel.archiveAttendance.value
    val selectedAttendance = viewModel.selectedArchiveItems.value
    LaunchedEffect(key1 = true) {
        viewModel.getAllArchiveAttendance()
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(grid_1),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically

    ) {
        val unarchive = ImageIconModel(
            imageVector = Icons.Outlined.Unarchive,
            contentDescription = R.string.unArchive,
            onClick = {
                viewModel.onEvent(
                    AttendanceEvent.ArchiveScreenUnArchiveSelectedItems
                )
            },
            tint = if (selectedAttendance.isEmpty()) MaterialTheme.colorScheme.captionColor else MaterialTheme.colorScheme.primary
        )
        val delete = ImageIconModel(
            imageVector = Icons.Outlined.DeleteForever,
            contentDescription = R.string.unArchive,
            onClick = {
                viewModel.onEvent(
                    AttendanceEvent.ArchiveScreenDeleteSelectedItems
                )
            },
            tint = if (selectedAttendance.isEmpty()) MaterialTheme.colorScheme.captionColor else MaterialTheme.colorScheme.primary
        )
        TriStateCheckbox(
            state = when {
                selectedAttendance.isEmpty() -> ToggleableState.Off
                archiveSubject.size == selectedAttendance.size -> ToggleableState.On
                else -> ToggleableState.Indeterminate
            },
            onClick = {
                viewModel.onEvent(
                    AttendanceEvent.ArchiveSelectAllClick(
                        archiveSubject,
                        selectedAttendance.isEmpty()
                    )
                )
            }
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(end = grid_2),
            text = stringResource(id = R.string.count_selected, selectedAttendance.size),
            color = MaterialTheme.colorScheme.onSurface
        )
        ImageIconButton(iconModel = unarchive)
        ImageIconButton(iconModel = delete)
        Spacer(modifier = Modifier.width(grid_1))
    }
    LazyColumn {
        items(items = archiveSubject, key = { it1 -> it1.subject + it1.id }) { model ->
            AttendanceItem(
                modifier = Modifier.animateItemPlacement(
                    animationSpec = spring(
                        dampingRatio = 2f, stiffness = 600f
                    )
                ),
                model = model,
                isCheckBoxVisible = true,
                onSelect = { clickItems, isSelected ->
                    viewModel.onEvent(
                        AttendanceEvent.ArchiveItemClick(
                            clickItems,
                            isSelected
                        )
                    )
                },
                isItemIsSelected = selectedAttendance.contains(model)
            )
        }
    }

    BottomPadding()
    BottomPadding()

}

@Preview(showBackground = true)
@Composable
fun BottomSheetArchivePreview() {
    BITAppTheme {
//        BottomSheetArchive()
    }
}