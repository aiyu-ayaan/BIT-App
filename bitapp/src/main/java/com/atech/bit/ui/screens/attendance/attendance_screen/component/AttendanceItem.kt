/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.attendance.attendance_screen.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atech.bit.R
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.SwipeGreen
import com.atech.bit.ui.theme.SwipeRed
import com.atech.bit.ui.theme.dividerOrCardColor
import com.atech.bit.ui.theme.grid_0_5
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.utils.calculatedDays
import com.atech.bit.utils.findPercentage
import com.atech.bit.utils.getRelativeDateForAttendance
import com.atech.bit.utils.setResources
import com.atech.core.datasource.room.attendance.AttendanceModel
import kotlin.math.ceil

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AttendanceItem(
    modifier: Modifier = Modifier,
    model: AttendanceModel,
    minPercentage: Int = 75,
    onTickOrCrossClickClick: (AttendanceModel, Boolean) -> Unit = { _, _ -> },
    onClick: (AttendanceModel) -> Unit = {},
    onLongClick: (AttendanceModel) -> Unit = {},
    onSelect: (AttendanceModel, Boolean) -> Unit = { _, _ -> },
    isCheckBoxVisible: Boolean = false,
    isItemIsSelected: Boolean = false,
    isFromBottomSheet : Boolean = false
) {
    var status by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    Surface(
        modifier = modifier
            .combinedClickable(
                onClick = {
                    if (isCheckBoxVisible) {
                        onSelect(model, !isItemIsSelected)
                    } else
                        onClick.invoke(model)
                },
                onLongClick = {
                    onLongClick.invoke(model)
                }
            ),
        color = if (isFromBottomSheet) BottomSheetDefaults.ContainerColor else MaterialTheme.colorScheme.surface
    ) {
        val percentage by remember(model) {
            mutableFloatStateOf(
                findPercentage(
                    model.present.toFloat(), model.total.toFloat()
                ) { present, total ->
                    when (total) {
                        0.0F -> 0.0F
                        else -> (present / total) * 100
                    }
                }
            )
        }

        setResources(percentage.toInt()) { per ->
            when {
                per == 0 -> {
                    status = context.getString(
                        R.string.status,
                    )
                }

                per >= minPercentage -> {
                    val day = calculatedDays(
                        model.present, model.total
                    ) { present, total ->
                        (((100 * present) - (minPercentage * total)) / minPercentage)
                    }.toInt()
                    status = when {
                        per == minPercentage || day <= 0 -> context.getString(
                            R.string.on_track
                        )

                        else -> context.getString(
                            R.string.leave_class, day.toString()
                        )
                    }
                }

                per < minPercentage -> {
                    val day = calculatedDays(
                        model.present, model.total
                    ) { present, total ->
                        (((minPercentage * total) - (100 * present)) / (100 - minPercentage))
                    }
                    status = context.getString(
                        R.string.attend_class, (ceil(day).toInt()).toString()
                    )
                }
            }
        }

        Card(
            modifier = modifier
                .padding(horizontal = grid_1, vertical = grid_0_5)
                .animateContentSize()
                .fillMaxWidth(), colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.dividerOrCardColor
            )
        ) {
            Row(
                modifier = Modifier.animateContentSize()
            ) {
                AnimatedVisibility(visible = isCheckBoxVisible) {
                    Checkbox(checked = isItemIsSelected, onCheckedChange = {
                        onSelect(model, it)
                    })
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .padding(grid_1),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (checkForLab(
                                        model.subject
                                    )
                                ) Icons.Default.Computer else Icons.AutoMirrored.Default.MenuBook,
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                                tint = when {
                                    percentage == 0.0F -> MaterialTheme.colorScheme.primary
                                    percentage >= minPercentage -> MaterialTheme.colorScheme.primary
                                    else -> SwipeRed
                                },
                            )
                            Text(
                                text = "${model.present}/${model.total}",
                                style = MaterialTheme.typography.bodySmall,
                                color = when {
                                    percentage == 0.0F -> MaterialTheme.colorScheme.primary
                                    percentage >= minPercentage -> MaterialTheme.colorScheme.primary
                                    else -> SwipeRed
                                },
                            )
                        }
                        Spacer(modifier = Modifier.width(grid_1))
                        Column(
                            modifier = Modifier.weight(1f), // Set weight to allow text to take available space
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                modifier = Modifier
                                    .basicMarquee(),
                                text = model.subject,
                                style = MaterialTheme.typography.titleMedium,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                            )

                            Spacer(modifier = Modifier.height(grid_1))
                            Text(
                                text = (if (model.teacher == null || model.teacher?.isEmpty() == true) "No teacher name " else model.teacher).toString(),
                                color = when {
                                    percentage == 0.0F -> MaterialTheme.colorScheme.primary
                                    percentage >= minPercentage -> MaterialTheme.colorScheme.primary
                                    else -> SwipeRed
                                },
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                        }
                        Spacer(modifier = Modifier.width(grid_1))
                        Box(modifier = Modifier) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(60.dp),
                                progress = {
                                    if (model.total == 0) 0.0F else (model.present.toFloat() / model.total.toFloat())
                                },
                                trackColor = MaterialTheme.colorScheme.surface,
                                strokeCap = StrokeCap.Round,
                                strokeWidth = grid_1,
                                color = if (percentage >= minPercentage) MaterialTheme.colorScheme.primary else SwipeRed,
                            )
                            Text(
                                text = "${percentage.toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.align(Alignment.Center),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    val isVisible = model.days.presetDays.isNotEmpty()
                    AnimatedVisibility(visible = isVisible) {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Last Attended : ${
                                    if (model.days.presetDays.isNotEmpty())
                                        model.days.presetDays.last().getRelativeDateForAttendance()
                                    else
                                        "Not started yet !!"
                                }",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = !isCheckBoxVisible,
                        enter = slideInVertically() + fadeIn(),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = status,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(.4f),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    null,
                                    modifier = Modifier
                                        .size(35.dp)
                                        .clickable { onTickOrCrossClickClick.invoke(model, true) },
                                    tint = SwipeGreen
                                )
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    null,
                                    modifier = Modifier
                                        .size(35.dp)
                                        .clickable { onTickOrCrossClickClick.invoke(model, false) },
                                    tint = SwipeRed
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


fun checkForLab(model: String) =
    (model.lowercase().contains("lab")
            || model.lowercase().contains("practical")
            || model.lowercase().contains("tutorial")
            || model.lowercase().contains("prac"))


@Preview(
    name = "Light", showBackground = true
)
@Composable
fun AttendanceItemPreview() {
    BITAppTheme {
        AttendanceItem(
            model = AttendanceModel(
                subject = "Logical Organisation of Computer",
                total = 10,
                present = 10,
                teacher = "BK Singh Sir",
            )
        )
    }
}