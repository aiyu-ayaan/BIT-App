/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.library.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAlert
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Unpublished
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atech.bit.ui.comman.ImageIconButton
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.SwipeGreen
import com.atech.bit.ui.theme.borderColor
import com.atech.bit.ui.theme.dividerOrCardColor
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.ui.theme.grid_2
import com.atech.core.datasource.room.library.LibraryModel

@Composable
fun LibraryItem(
    modifier: Modifier = Modifier,
    model: LibraryModel,
    onEditClick: (LibraryModel) -> Unit = {},
    onTickClick: (LibraryModel) -> Unit = {},
    onDeleteClick: (LibraryModel) -> Unit = {},
) {
    val isIconVisible by remember(model) {
        mutableStateOf(
            model.markAsReturn || model.eventId != -1L
        )
    }
    var isExpand by rememberSaveable {
        mutableStateOf(
            false
        )
    }
    Surface(
        modifier = modifier
            .clickable {
                isExpand = !isExpand
            }
    ) {
        OutlinedCard(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
                .fillMaxWidth()
                .padding(grid_1),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.borderColor),
        ) {
            Column(
                modifier = Modifier
                    .padding(grid_1)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = grid_1)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    AnimatedVisibility(visible = isIconVisible) {
                        Icon(
                            modifier = Modifier.weight(.1f), imageVector = when {
                                model.markAsReturn.not() && model.eventId != -1L -> Icons.Outlined.AddAlert
                                model.markAsReturn -> Icons.Outlined.Check
                                else -> Icons.Outlined.CalendarMonth
                            }, contentDescription = null, tint = when {
                                model.markAsReturn.not() && model.eventId != -1L -> MaterialTheme.colorScheme.primary
                                model.markAsReturn -> SwipeGreen
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                    }
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = if (isIconVisible) grid_1 else 0.dp),
                        text = model.bookName,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.width(grid_1))
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.3f),
                        textAlign = TextAlign.End,
                        text = model.returnFormatData,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.height(grid_1))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = grid_1),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier, horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            modifier = Modifier,
                            text = model.bookId,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            modifier = Modifier,
                            text = "Issued on : " + model.issueFormatData,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    FloatingActionButton(onClick = {
                        onEditClick(model)
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Edit, contentDescription = null
                        )
                    }
                }
                if (isExpand) {
                    Spacer(modifier = Modifier.height(grid_2))
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.dividerOrCardColor
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ImageIconButton(
                            modifier = Modifier.weight(.5f),
                            icon = if (model.markAsReturn) Icons.Outlined.Unpublished
                            else Icons.Outlined.CheckCircleOutline,
                            tint = MaterialTheme.colorScheme.primary,
                            onClick = {
                                onTickClick(model)
                            }
                        )
                        ImageIconButton(
                            modifier = Modifier.weight(.5f),
                            icon = Icons.Outlined.Delete,
                            tint = MaterialTheme.colorScheme.error,
                            onClick = {
                                onDeleteClick(model)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LibraryItemPreview() {
    BITAppTheme {
        LibraryItem(
            model = LibraryModel(
                bookId = "CA402",
                bookName = "Computer Architecture",
                issueDate = 1627770600000,
                returnDate = 1627770600000,
                /*alertDate = 1627770600000,
                eventId = 1627770600000,*/
            )
        )
    }
}