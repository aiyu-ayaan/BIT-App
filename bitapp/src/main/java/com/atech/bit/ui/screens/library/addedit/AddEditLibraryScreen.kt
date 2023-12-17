package com.atech.bit.ui.screens.library.addedit

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAlert
import androidx.compose.material.icons.outlined.Close
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atech.bit.ui.comman.BackToolbar
import com.atech.bit.ui.comman.EditText
import com.atech.bit.ui.comman.ImageIconButton
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

    val context = LocalContext.current

    val bookName by viewModel.bookName
    val bookId by viewModel.bookId
    val issueDate by viewModel.issueDate
    val returnDate by viewModel.returnDate
    var isEventVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var isDatePickerActive by rememberSaveable {
        mutableStateOf(false to PickFor.ISSUE_DATE)
    }

    BackHandler {
        if (isDatePickerActive.first) {
            isDatePickerActive = false to PickFor.ISSUE_DATE
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
            EditText(modifier = Modifier.fillMaxWidth(),
                value = bookName,
                placeholder = "Book Name",
                supportingMessage = "Required",
                onValueChange = { value ->
                    viewModel.onEvent(
                        LibraryEvent.OnBookNameChange(
                            value
                        )
                    )
                })
            Spacer(modifier = Modifier.padding(grid_1))
            EditText(modifier = Modifier.fillMaxWidth(),
                value = bookId,
                placeholder = "Book Id",
                supportingMessage = "Optional",
                onValueChange = { value ->
                    viewModel.onEvent(
                        LibraryEvent.OnBookIdChange(
                            value
                        )
                    )
                })
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
                            isDatePickerActive = true to PickFor.ISSUE_DATE
                        },
                    readOnly = true
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
                            isDatePickerActive = true to PickFor.RETURN_DATE
                        }
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    EditText(
                        modifier = Modifier.weight(1f),
                        value = "",
                        placeholder = "Reminder",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.AddAlert,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = null,
                    )
                    ImageIconButton(
                        icon = Icons.Outlined.Close,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { /*TODO*/ }, modifier = Modifier.weight(1f)
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

        if (isDatePickerActive.first) LibraryDatePickerDialog(onDismissRequest = {
            isDatePickerActive = false to PickFor.ISSUE_DATE
        }, onDateSelected = { date ->
            viewModel.onEvent(LibraryEvent.PickDateClick(isDatePickerActive.second, date))
        })
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
    onDismissRequest: () -> Unit = {}, onDateSelected: (Long) -> Unit = {}
) {
    val datePickerState = rememberDatePickerState()
    val confirmEnabled = remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }
    DatePickerDialog(onDismissRequest = onDismissRequest, confirmButton = {
        TextButton(
            onClick = {
                onDateSelected(datePickerState.selectedDateMillis!!)
                onDismissRequest()
            }, enabled = confirmEnabled.value
        ) {
            Text("OK")
        }
    }, dismissButton = {
        TextButton(
            onClick = onDismissRequest
        ) {
            Text("Cancel")
        }
    }) {
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
