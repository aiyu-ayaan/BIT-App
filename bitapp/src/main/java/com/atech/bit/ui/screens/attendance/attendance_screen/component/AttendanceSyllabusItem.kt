package com.atech.bit.ui.screens.attendance.attendance_screen.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.atech.bit.ui.comman.ImageIconButton
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.grid_0_5
import com.atech.bit.ui.theme.grid_1
import com.atech.core.usecase.SyllabusUIModel

@Composable
fun AttendanceSyllabusItem(
    modifier: Modifier = Modifier,
    model: SyllabusUIModel,
    isOnline: Boolean = false,
    onClick: (SyllabusUIModel, Boolean) -> Unit = { _, _ -> },
    onEditClick: (SyllabusUIModel) -> Unit = {}
) {
    var isChecked by rememberSaveable {
        mutableStateOf(
            if (isOnline) model.isFromOnline
            else model.isAdded ?: false
        )
    }
    Surface(
        modifier = modifier
            .padding(
                top = grid_1,
            )
            .clickable {
                isChecked = !isChecked
                onClick(model, isChecked)
            }
            .animateContentSize()
    ) {
        OutlinedCard(
            modifier = Modifier
                .padding(horizontal = grid_1)
                .fillMaxWidth(),
            colors = CardDefaults
                .cardColors(
                    containerColor = MaterialTheme
                        .colorScheme.surface
                )
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = grid_0_5, horizontal = grid_1)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = model.subject,
                    style = MaterialTheme.typography
                        .bodyLarge,
                    modifier = Modifier
                        .weight(1f)
                )
                AnimatedVisibility(visible = isChecked) {
                    ImageIconButton(
                        icon = Icons.Outlined.Edit,
                        onClick = { onEditClick.invoke(model) }
                    )
                }
                Checkbox(checked = isChecked, onCheckedChange = {
                    isChecked = it
                })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AttendanceSyllabusItemsPreview() {
    BITAppTheme {
//        AttendanceSyllabusItem(
//
//        )
    }
}