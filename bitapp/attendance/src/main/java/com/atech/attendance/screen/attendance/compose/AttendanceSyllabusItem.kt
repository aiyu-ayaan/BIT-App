package com.atech.attendance.screen.attendance.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
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
import com.atech.components.ImageIconButton
import com.atech.core.use_case.SyllabusUIModel
import com.atech.theme.BITAppTheme
import com.atech.theme.grid_0_5
import com.atech.theme.grid_1

@Composable
fun AttendanceSyllabusItem(
    modifier: Modifier = Modifier,
    model: SyllabusUIModel,
) {
    var isChecked by rememberSaveable {
        mutableStateOf(model.isFromOnline)
    }
    Surface(
        modifier = modifier
            .padding(
                top = grid_1,
            )
            .clickable {
//                Todo handle click
            }
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
                    .padding(vertical = grid_0_5 , horizontal = grid_1)
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
                ImageIconButton(
                    icon = Icons.Outlined.Edit
                )
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