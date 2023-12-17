package com.atech.bit.ui.screens.attendance.attendance_screen.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.atech.bit.R
import com.atech.bit.ui.screens.attendance.attendance_screen.AttendanceEvent
import com.atech.bit.ui.screens.attendance.attendance_screen.AttendanceViewModel
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.captionColor
import com.atech.bit.ui.theme.grid_1
import com.atech.core.data_source.room.attendance.Sort
import com.atech.core.data_source.room.attendance.SortBy
import com.atech.core.data_source.room.attendance.SortOrder


@Composable
fun AttendanceSettingDialog(
    modifier: Modifier = Modifier, viewModel: AttendanceViewModel, onDismiss: () -> Unit = {}
) {
    val defPercentage = viewModel.defaultPercentage.value
    val defSortBy = viewModel.sort.value
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(
                ColorUtils.blendARGB(
                    MaterialTheme.colorScheme.surface.toArgb(),
                    MaterialTheme.colorScheme.primary.toArgb(),
                    .2f
                )
            )
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(grid_1),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var sliderValue by rememberSaveable {
                mutableFloatStateOf(defPercentage.toFloat() / 100)
            }
            Box {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center),
                    progress = sliderValue,
                    strokeWidth = 12.dp,
                    strokeCap = StrokeCap.Round
                )
                Text(
                    text = "${(sliderValue * 100).toInt()}%",
                    modifier = Modifier.align(alignment = Alignment.Center),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.captionColor,
                )
            }
            Spacer(modifier = Modifier.height(grid_1))
            Slider(
                value = sliderValue,
                onValueChange = {
                    sliderValue = it
                },
            )
            val sortByItems = SortBy.entries.toList()
                .map { it.name.lowercase().replaceFirstChar { ch -> ch.uppercase() } }
            var sortBy by remember {
                mutableStateOf(
                    defSortBy.sortBy.name.lowercase().replaceFirstChar { ch -> ch.uppercase() })
            }
            ExposedDropdown(list = sortByItems,
                expanded = false,
                label = "Sort by",
                value = sortBy,
                onSelect = {
                    sortBy = it
                })
            Spacer(modifier = Modifier.height(grid_1))
            val sortOrderItems = SortOrder.entries.toList()
                .map { it.name.lowercase().replaceFirstChar { ch -> ch.uppercase() } }
            var sortOrder by remember {
                mutableStateOf(
                    defSortBy.sortOrder.name.lowercase().replaceFirstChar { ch -> ch.uppercase() })
            }
            ExposedDropdown(list = sortOrderItems,
                expanded = false,
                label = "Sort Order",
                value = sortOrder,
                onSelect = {
                    sortOrder = it
                })
            Spacer(modifier = Modifier.height(grid_1))
            Row {
                Button(
                    onClick = {
                        viewModel.onEvent(
                            AttendanceEvent.UpdateSettings(
                                percentage = (sliderValue * 100).toInt(),
                                sort = Sort(
                                    sortBy = SortBy.valueOf(sortBy.uppercase()),
                                    sortOrder = SortOrder.valueOf(sortOrder.uppercase())
                                )
                            )
                        )
                        onDismiss.invoke()
                    }, modifier = Modifier
                        .weight(1f)
                        .padding(grid_1)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Save,
                        contentDescription = stringResource(id = R.string.save)
                    )
                    Spacer(modifier = Modifier.width(grid_1))
                    Text(text = stringResource(id = R.string.save))
                }
                OutlinedButton(
                    onClick = onDismiss, modifier = Modifier
                        .weight(1f)
                        .padding(grid_1)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Cancel,
                        contentDescription = stringResource(id = R.string.cancel),
                    )
                    Spacer(modifier = Modifier.width(grid_1))
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdown(
    modifier: Modifier = Modifier,
    list: List<String>,
    expanded: Boolean,
    label: String = "",
    value: String = "",
    onSelect: (String) -> Unit = {}
) {
    var isexpanded by remember { mutableStateOf(expanded) }
    ExposedDropdownMenuBox(modifier = modifier, expanded = isexpanded, onExpandedChange = {
        isexpanded = it
    }) {
        TextField(
            // The `menuAnchor` modifier must be passed to the text field for correctness.
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            readOnly = true,
            value = value,
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            ),
        )
        ExposedDropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = isexpanded,
            onDismissRequest = { isexpanded = false },
        ) {
            list.forEach { item ->
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    text = { Text(item) },
                    onClick = {
                        onSelect.invoke(item)
                        isexpanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AttendanceSettingDialogPreview() {
    BITAppTheme {
//        AttendanceSettingDialog()
    }
}