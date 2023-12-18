package com.atech.bit.ui.screens.library.addedit

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAlert
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atech.bit.ui.comman.BackToolbar
import com.atech.bit.ui.comman.EditText
import com.atech.bit.ui.comman.clickable
import com.atech.bit.ui.screens.library.LibraryEvent
import com.atech.bit.ui.screens.library.LibraryManagerViewModel
import com.atech.bit.ui.screens.library.PickFor
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.grid_1
import com.atech.core.utils.EDIT_TEXT_DATE_FORMAT
import com.atech.core.utils.convertLongToTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditLibraryScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: LibraryManagerViewModel = hiltViewModel()
) {

    val (hasError, errorMessage) = viewModel.harError.value
    val bookName by viewModel.bookName
    val bookId by viewModel.bookId
    val issueDate by viewModel.issueDate
    val returnDate by viewModel.returnDate
    val hasSubjectError by viewModel.hasSubjectError
    val hasIssueDateError by viewModel.hasIssueDateError
    var isEventVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var isDatePickerActive by rememberSaveable {
        mutableStateOf(Triple(false, PickFor.ISSUE_DATE, -1L))
    }

    BackHandler {
        if (isDatePickerActive.first) {
            isDatePickerActive = Triple(false, PickFor.ISSUE_DATE, -1L)
        } else
            backPressed(viewModel, navController)

    }
    Scaffold(
        modifier = modifier,
        topBar = {
            BackToolbar(title = "Edit", onNavigationClick = {
                backPressed(viewModel, navController)
            })
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(grid_1)
        ) {
            EditText(
                modifier = Modifier.fillMaxWidth(),
                value = bookName,
                placeholder = "Book Name",
                supportingMessage = "Required",
                onValueChange = { value ->
                    viewModel.onEvent(
                        LibraryEvent.OnBookNameChange(
                            value
                        )
                    )
                },
                errorMessage = "Can't be empty !!",
                isError = hasSubjectError,
                clearIconClick = {
                    viewModel.onEvent(
                        LibraryEvent.OnBookNameChange(
                            ""
                        )
                    )
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                )
            )
            Spacer(modifier = Modifier.padding(grid_1))
            EditText(
                modifier = Modifier.fillMaxWidth(),
                value = bookId,
                placeholder = "Book Id",
                supportingMessage = "Optional",
                onValueChange = { value ->
                    viewModel.onEvent(
                        LibraryEvent.OnBookIdChange(
                            value
                        )
                    )
                },
                clearIconClick = {
                    viewModel.onEvent(
                        LibraryEvent.OnBookIdChange(
                            ""
                        )
                    )
                },
                maxLines = 2,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),

                )
            Spacer(modifier = Modifier.padding(grid_1))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                EditText(
                    modifier = Modifier.weight(1f),
                    value = issueDate.let { date ->
                        if (date == -1L) "" else date.convertLongToTime(
                            EDIT_TEXT_DATE_FORMAT
                        )
                    },
                    placeholder = "Issue On",
                    supportingMessage = "Required",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = null,
                    onValueChange = { value ->
                        viewModel.onEvent(
                            LibraryEvent.OnIssueDateChange(
                                value.toLong()
                            )
                        )
                    },
                    interactionSource = remember { MutableInteractionSource() }
                        .clickable {
                            isDatePickerActive = Triple(true, PickFor.ISSUE_DATE, issueDate)
                        },
                    readOnly = true,
                    errorMessage = "Can't be empty !!",
                    isError = hasIssueDateError
                )
                Spacer(modifier = Modifier.padding(grid_1))
                EditText(
                    modifier = Modifier.weight(1f),
                    value = returnDate.let { date ->
                        if (date == -1L) "" else date.convertLongToTime(
                            EDIT_TEXT_DATE_FORMAT
                        )
                    },
                    placeholder = "Return By",
                    supportingMessage = "Required",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = null,
                    onValueChange = { value ->
                        viewModel.onEvent(
                            LibraryEvent.OnReturnDateChange(
                                value.toLong()
                            )
                        )
                    },
                    readOnly = true,
                    interactionSource = remember { MutableInteractionSource() }
                        .clickable {
                            isDatePickerActive = Triple(true, PickFor.RETURN_DATE, returnDate)
                        },
                    errorMessage = errorMessage,
                    isError = hasError
                )
            }
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { isEventVisible = !isEventVisible }) {
                Text(
                    modifier = Modifier.padding(grid_1), text = "Add Event"
                )
            }
            AnimatedVisibility(visible = isEventVisible) {
                EditText(
                    modifier = Modifier.
                    fillMaxWidth(),
                    value = "",
                    placeholder = "Reminder",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.AddAlert,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    readOnly = true,
                    interactionSource = remember {
                        MutableInteractionSource()
                    }.clickable {

                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        viewModel.onEvent(LibraryEvent.SaveBook)
                        backPressed(viewModel, navController)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !hasError
                ) {
                    Text(text = "Save")
                }
                Spacer(modifier = Modifier.width(grid_1))
                TextButton(
                    onClick = {
                        backPressed(viewModel, navController)
                    }, modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Cancel")
                }
            }
        }

        if (isDatePickerActive.first) LibraryDatePickerDialog(
            onDismissRequest = {
                isDatePickerActive = Triple(false, PickFor.ISSUE_DATE, issueDate)
            },
            onDateSelected = { date ->
                viewModel.onEvent(LibraryEvent.PickDateClick(isDatePickerActive.second, date))
            },
            selectedDate = isDatePickerActive.third
        )
    }
}

private fun backPressed(
    viewModel: LibraryManagerViewModel,
    navController: NavController
) {
    viewModel.onEvent(LibraryEvent.ResetValue)
    navController.navigateUp()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryDatePickerDialog(
    onDismissRequest: () -> Unit = {},
    onDateSelected: (Long) -> Unit = {},
    selectedDate: Long
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = if (selectedDate == -1L) null
        else selectedDate
    )
    val confirmEnabled = remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }
    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis!!)
                    onDismissRequest()
                }, enabled = confirmEnabled.value
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Preview(showBackground = true)
@Composable
private fun AddEditLibraryScreenPreview() {
    BITAppTheme {
        AddEditLibraryScreen()
    }
}
