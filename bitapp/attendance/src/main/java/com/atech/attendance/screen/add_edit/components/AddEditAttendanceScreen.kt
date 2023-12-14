package com.atech.attendance.screen.add_edit.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
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
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAttendanceScreen(
    modifier: Modifier = Modifier,
    viewModel: AddEditViewModel = hiltViewModel(),
    navController: NavController = rememberNavController()
) {

    val snackBarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(key1 = true) {
        viewModel.oneTimeEvent.collectLatest { event ->
            when (event) {
                is AddEditViewModel.AddEditOneTimeEvent.ShowSnackBar ->
                    snackBarHostState.showSnackbar(
                        message = event.message
                    )
            }
        }
    }

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
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            BackToolbar(
                title = if (viewModel.isEdit) "Edit" else "Add",
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
            EditText(modifier = Modifier.fillMaxWidth(),
                value = subject,
                placeholder = stringResource(id = R.string.subject),
                supportingMessage = stringResource(id = R.string.required),
                onValueChange = { sub ->
                    if (isSubjectIsEmpty) {
                        isSubjectIsEmpty = false
                    }
                    viewModel.onEvent(AddEditEvent.OnSubjectChange(sub))
                },
                errorMessage = stringResource(id = R.string.required),
                isError = isSubjectIsEmpty,
                clearIconClick = {
                    viewModel.onEvent(AddEditEvent.OnSubjectChange(""))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                ),
                focusRequester =
                if (viewModel.isEdit) null else
                    remember {
                        FocusRequester()
                    }
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
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
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
                    if (typedPresent.length > 3) return@EditText
                    try {
                        viewModel.onEvent(
                            AddEditEvent.OnPresentChange(
                                if (typedPresent.isBlank()) 0 else typedPresent.toInt()
                            )
                        )
                        if (typedPresent.isNotBlank() && typedPresent.toInt() > total) {
                            isPresentHaveError = true
                        }
                    } catch (e: Exception) {
                        AddEditEvent.OnPresentChange(
                            0
                        )
                    }

                },
                trailingIcon = {
                    if (present != 0) Icon(imageVector = if (isPresentHaveError) Icons.Outlined.FlipCameraAndroid else Icons.Outlined.Clear,
                        contentDescription = null,
                        tint = if (isPresentHaveError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            if (isPresentHaveError) {
                                viewModel.onEvent(AddEditEvent.OnTotalChange(present))
                                viewModel.onEvent(AddEditEvent.OnPresentChange(total))
                                isPresentHaveError = false
                            } else viewModel.onEvent(AddEditEvent.OnPresentChange(0))
                        })
                },
                isError = isPresentHaveError,
                errorMessage = "Check your input ($present > $total)\nPress error button to flip the values !!",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
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
                    if (typedTotal.length > 3) return@EditText
                    try {
                        viewModel.onEvent(
                            AddEditEvent.OnTotalChange(
                                if (typedTotal.isBlank()) 0 else typedTotal.toInt()
                            )
                        )
                        if (typedTotal.isNotBlank() && typedTotal.toInt() >= present) {
                            isPresentHaveError = false
                        }
                        if (typedTotal.isNotBlank() && typedTotal.toInt() < present) {
                            isPresentHaveError = true
                        }
                    } catch (e: Exception) {
                        AddEditEvent.OnTotalChange(
                            0
                        )
                    }
                },
                clearIconClick = {
                    viewModel.onEvent(AddEditEvent.OnTotalChange(0))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                )
            )
            Spacer(modifier = Modifier.height(grid_1))
            Row {
                Button(
                    onClick = {
                        if (subject.isBlank()) {
                            isSubjectIsEmpty = true
                            return@Button
                        }
                        isSubjectIsEmpty = false
                        viewModel.onEvent(AddEditEvent.OnSaveClick {
                            navController.navigateUp()
                        })
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
    showBackground = false, uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AddEditAttendanceScreenPreview() {
    BITAppTheme {
        AddEditAttendanceScreen()
    }
}