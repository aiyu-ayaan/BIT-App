/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.comman

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowDown
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atech.bit.R
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.dividerOrCardColor
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.ui.theme.grid_2
import com.atech.core.datasource.firebase.remote.model.CourseDetailModel
import com.atech.core.datasource.firebase.remote.model.CourseDetails
import com.atech.core.datasource.firebase.remote.model.toMap


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseSemBottomSheet(
    modifier: Modifier = Modifier,
    model: CourseDetails,
    course: String = "BCA",
    sem: String = "3",
    onDismissRequest: () -> Unit = {},
    onSaveClick: (Pair<String, String>) -> Unit = {}
) {
    var currentSelectedChip by remember {
        mutableStateOf(
            course to sem
        )
    }
    ModalBottomSheet(modifier = modifier, onDismissRequest = onDismissRequest, dragHandle = {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(grid_2),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.clickable { onDismissRequest.invoke() }) {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardDoubleArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        modifier = Modifier.padding(start = grid_1),
                        text = "Course Preference",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                ImageIconButton(icon = Icons.Outlined.Save,
                    tint = MaterialTheme.colorScheme.primary,
                    onClick = {
                        onSaveClick(currentSelectedChip)
                        onDismissRequest.invoke()
                    })
            }
            Divider(
                color = MaterialTheme.colorScheme.dividerOrCardColor
            )
        }
    }) {
        ChooseSemBottomSheetScreen(model = model,
            course = currentSelectedChip.first,
            sem = currentSelectedChip.second,
            onClick = {
                currentSelectedChip = it
            })
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseSemBottomSheetScreen(
    modifier: Modifier = Modifier,
    model: CourseDetails,
    course: String = "BCA",
    sem: String = "3",
    onClick: (Pair<String, String>) -> Unit = { }
) {
    val mapList = model.toMap()
    Column(
        modifier = modifier
            .padding(grid_1),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnim(
            modifier = Modifier.size(200.dp), res = R.raw.choose_sem
        )
        Spacer(modifier = Modifier.height(grid_1))
        Text(text = "Choose your Course !!")
        Spacer(modifier = Modifier.height(grid_1))
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            mapList.keys.forEach {
                val isSelected = course == it
                FilterChip(selected = isSelected,
                    modifier = Modifier.padding(end = grid_1),
                    onClick = {
                        onClick(it to "1")
                    },
                    label = { Text(it) },
                    leadingIcon = {
                        Icon(
                            if (isSelected) Icons.Filled.Done
                            else Icons.Filled.Book,
                            contentDescription = "Localized description",
                            Modifier.size(AssistChipDefaults.IconSize),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    })
            }
        }
        Spacer(modifier = Modifier.height(grid_1))
        Text(text = "Choose your Semester !!")
        Spacer(modifier = Modifier.height(grid_1))
        val maxSem = mapList[course]!!.toInt()
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            (1..maxSem).forEach {
                val isSelected = sem == it.toString()
                FilterChip(
                    selected = isSelected,
                    modifier = Modifier.padding(end = grid_1),
                    onClick = {
                        onClick(course to it.toString())
                    },
                    label = { Text("$it") },
                )
            }
        }
        BottomPadding()
        BottomPadding()
    }
}


@Preview(showBackground = true)
@Composable
private fun ChooseSemBottomSheetPreview() {
    BITAppTheme {
        ChooseSemBottomSheetScreen(
            model = CourseDetails(
                course = listOf(
                    CourseDetailModel(
                        name = "BCA", sem = 6
                    ),
                    CourseDetailModel(
                        name = "MCA", sem = 4
                    ),
                )
            )
        )
    }
}