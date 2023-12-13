package com.atech.attendance.screen.add_edit.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.FlipCameraAndroid
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Start
import androidx.compose.material.icons.outlined.Summarize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atech.attendance.R
import com.atech.attendance.component.EditText
import com.atech.attendance.screen.add_edit.AddEditEvent
import com.atech.attendance.screen.add_edit.AddEditViewModel
import com.atech.components.BackToolbar
import com.atech.theme.BITAppTheme
import com.atech.theme.grid_1


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAttendanceScreen(
    modifier: Modifier = Modifier,
    viewModel: AddEditViewModel = hiltViewModel(),
    navController: NavController = rememberNavController()
) {
    val subject = viewModel.subject.value
    val teacherName = viewModel.teacherName.value
    val present = viewModel.present.value
    val total = viewModel.total.value

    var isSubjectIsEmpty by remember {
        mutableStateOf(false)
    }

    var isPresentHaveError by remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            BackToolbar(
                title = "Add",
                onNavigationClick = {
                    navController.navigateUp()
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(grid_1)
        ) {
            EditText(
                modifier = Modifier.fillMaxWidth(),
                value = subject,
                placeholder = stringResource(id = R.string.subject),
                supportingMessage = stringResource(id = R.string.required),
                onValueChange = { sub ->
                    viewModel.onEvent(AddEditEvent.OnSubjectChange(sub))
                },
                errorMessage = stringResource(id = R.string.required),
                isError = isSubjectIsEmpty,
                clearIconClick = {
                    viewModel.onEvent(AddEditEvent.OnSubjectChange(""))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )
            Spacer(modifier = Modifier.height(grid_1))
            EditText(
                modifier = Modifier.fillMaxWidth(),
                value = teacherName,
                placeholder = stringResource(id = R.string.teacher_name),
                supportingMessage = stringResource(id = R.string.optional),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                onValueChange = { teacher ->
                    viewModel.onEvent(AddEditEvent.OnTeacherNameChange(teacher))
                },
                clearIconClick = {
                    viewModel.onEvent(AddEditEvent.OnTeacherNameChange(""))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )
            Spacer(modifier = Modifier.height(grid_1))
            EditText(
                modifier = Modifier.fillMaxWidth(),
                value = present.takeIf { it1 -> it1 != 0 }?.toString() ?: "",
                placeholder = stringResource(id = R.string.initail_present),
                supportingMessage = stringResource(id = R.string.edit_text_empty_messge_for_days),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Start,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                maxLines = 1,
                onValueChange = { typedPresent ->
                    if (present.toString().length != 3) {
                        viewModel.onEvent(AddEditEvent.OnPresentChange(typedPresent.toInt()))
                        if (typedPresent.toInt() > total) {
                            isPresentHaveError = true
                        }
                    }
                },
                clearIconClick = {
                    if (!isPresentHaveError)
                        viewModel.onEvent(AddEditEvent.OnPresentChange(0))
                    else {
                        viewModel.onEvent(AddEditEvent.OnTotalChange(present))
                        viewModel.onEvent(AddEditEvent.OnPresentChange(total))
                        isPresentHaveError = false
                    }

                },
                trailingIcon = {
                    Icon(
                        imageVector = if (isPresentHaveError) Icons.Outlined.FlipCameraAndroid else Icons.Outlined.Clear,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                isError = isPresentHaveError,
                errorMessage = "Check your input ($present > $total)\nPress error button to flip the values !!",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )
            Spacer(modifier = Modifier.height(grid_1))
            EditText(
                modifier = Modifier.fillMaxWidth(),
                value = total.takeIf { it1 -> it1 != 0 }?.toString() ?: "",
                placeholder = stringResource(id = R.string.total_days),
                supportingMessage = stringResource(id = R.string.edit_text_empty_messge_for_days),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Summarize,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                maxLines = 1,
                onValueChange = { typedTotal ->
                    if (total.toString().length != 3)
                        viewModel.onEvent(AddEditEvent.OnTotalChange(typedTotal.toInt()))
                },
                clearIconClick = {
                    viewModel.onEvent(AddEditEvent.OnTotalChange(0))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )
            Spacer(modifier = Modifier.height(grid_1))
            Row {
                Button(
                    onClick = {
                        viewModel.onEvent(AddEditEvent.OnSaveClick)
                    },
                    modifier = Modifier
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
                    onClick = { navController.navigateUp() },
                    modifier = Modifier
                        .weight(1f)
                        .padding(grid_1)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Save,
                        contentDescription = stringResource(id = R.string.cancel),
                    )
                    Spacer(modifier = Modifier.width(grid_1))
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AddEditAttendanceScreenPreview() {
    BITAppTheme {
        AddEditAttendanceScreen()
    }
}