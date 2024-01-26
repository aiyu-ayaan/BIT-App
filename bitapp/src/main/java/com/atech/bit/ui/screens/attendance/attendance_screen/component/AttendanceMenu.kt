/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.attendance.attendance_screen.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Undo
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.atech.bit.ui.comman.BottomPadding
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.captionColor
import com.atech.bit.ui.theme.dividerOrCardColor
import com.atech.bit.ui.theme.grid_2
import com.atech.bit.ui.theme.grid_3
import com.atech.core.datasource.room.attendance.AttendanceModel

@Composable
fun AttendanceMenu(
    modifier: Modifier = Modifier,
    attendanceModel: AttendanceModel,
    isUndoEnable: Boolean = true,
    onUndoClick: (AttendanceModel) -> Unit = {},
    onEditClick: (AttendanceModel) -> Unit = {},
    onArchiveClick: (AttendanceModel) -> Unit = {},
    onDeleteClick: (AttendanceModel) -> Unit = {},
    commonAction: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
    ) {
        Text(
            text = attendanceModel.subject,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(horizontal = grid_2)
                .padding(bottom = grid_2),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
        Divider(
            color = MaterialTheme.colorScheme.dividerOrCardColor
        )

        val items = listOf(
            MenuItem(
                icon = Icons.Outlined.Undo,
                text = "Undo",
                onClick = {
                    onUndoClick(attendanceModel)
                    commonAction()
                },
                isEnable = isUndoEnable
            ),
            MenuItem(
                icon = Icons.Outlined.Edit,
                text = "Edit",
                onClick = {
                    onEditClick(attendanceModel)
                    commonAction()
                }
            ),
            MenuItem(
                icon = Icons.Outlined.Archive,
                text = "Archive",
                onClick = {
                    onArchiveClick(attendanceModel)
                    commonAction()
                }
            ),
            MenuItem(
                icon = Icons.Outlined.Delete,
                text = "Delete",
                onClick = {
                    onDeleteClick(attendanceModel)
                    commonAction()
                }
            ),
        )
        items.forEach { item ->
            MenuItem(menuItem = item)
        }
        BottomPadding()
    }
}

@Composable
fun MenuItem(
    modifier: Modifier = Modifier,
    menuItem: MenuItem
) {
    Surface(
        modifier = modifier
            .let {
                if (menuItem.isEnable)
                    it.clickable {
                        menuItem.onClick()
                    }
                else it
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(grid_2),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = menuItem.icon,
                contentDescription = menuItem.text,
                tint = if (menuItem.isEnable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.captionColor,
            )
            Text(
                text = menuItem.text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(start = grid_3),
                color = if (menuItem.isEnable) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.captionColor
            )
        }
    }
}

data class MenuItem(
    val icon: ImageVector,
    val text: String,
    val onClick: () -> Unit = {},
    val isEnable: Boolean = true
)

@Preview(showBackground = true)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AttendanceMenuPreview() {
    BITAppTheme {
        AttendanceMenu(
            attendanceModel = AttendanceModel(
                subject = "Subject",
            )
        )
    }
}