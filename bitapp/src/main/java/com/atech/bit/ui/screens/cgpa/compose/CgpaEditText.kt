package com.atech.bit.ui.screens.cgpa.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Addchart
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.Subject
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.atech.bit.R
import com.atech.bit.ui.comman.EditText
import com.atech.bit.ui.screens.cgpa.CgpaEditModel
import com.atech.bit.ui.screens.cgpa.hasCgpaError
import com.atech.bit.ui.screens.cgpa.hasCreditError
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.captionColor
import com.atech.bit.ui.theme.dividerOrCardColor
import com.atech.bit.ui.theme.grid_1


@Composable
fun CgpaEditText(
    modifier: Modifier = Modifier,
    model: CgpaEditModel = CgpaEditModel(
        semPlaceHolder = "Sem 1"
    ),
    onValueChange: (CgpaEditModel) -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        var hasSemError by rememberSaveable {
            mutableStateOf(false)
        }
        var hasTotalGrade by rememberSaveable {
            mutableStateOf(false)
        }
        EditText(
            modifier = Modifier.weight(1f),
            value = if (model.semester.isEmpty() || model.semester == "0.0") "" else model.semester,
            placeholder = model.semPlaceHolder,
            onValueChange = {
                if (it.length > 4 || it.isEmpty()) return@EditText
                onValueChange(model.copy(semester = it))
                hasSemError = it.hasCgpaError()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            isError = hasSemError,
            errorMessage = stringResource(R.string.value_range_between_1_to_10),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.AutoGraph,
                    contentDescription = null
                )
            },
            clearIconClick = {
                onValueChange(model.copy(semester = "0.0"))
                hasSemError = false
            }
        )
        Spacer(modifier = Modifier.width(grid_1))
        EditText(
            modifier = Modifier.weight(1f),
            value = if (model.earnCredit.isEmpty() || model.earnCredit == "0.0") "" else model.earnCredit,
            placeholder = stringResource(R.string.total_credit),
            onValueChange = {
                if (it.length > 4) return@EditText
                onValueChange(model.copy(earnCredit = it))
                hasTotalGrade = it.hasCreditError()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            isError = hasTotalGrade,
            errorMessage = stringResource(R.string.value_range_between_1_to_10),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Subject,
                    contentDescription = null
                )
            },
            clearIconClick = {
                onValueChange(model.copy(earnCredit = "0.0"))
                hasTotalGrade = false
            }
        )
    }
}

@Composable
fun CgpaTitle(
    modifier: Modifier = Modifier,
    upToText: String = "For Text"
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.enter_your_sgpa_with_earn_credits),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = modifier.height(grid_1))
        Text(
            text = upToText,
            color = MaterialTheme.colorScheme.captionColor,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}


@Composable
fun CgpaFooter(
    modifier: Modifier = Modifier,
    onCalculate: () -> Unit = {},
    enable: Boolean = true,
    value: String = "",
    onValueChange: (String) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(grid_1))
        TextButton(
            enabled = enable,
            modifier = Modifier.fillMaxWidth(),
            onClick = onCalculate
        ) {
            Text(text = stringResource(R.string.calculate))
        }
        Spacer(modifier = Modifier.height(grid_1))
        EditText(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            placeholder = stringResource(R.string.calculated_cgpa),
            readOnly = true,
            onValueChange = onValueChange,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Addchart,
                    contentDescription = null
                )
            }
        )
        Spacer(modifier = Modifier.height(grid_1))
        Divider(
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.dividerOrCardColor
        )
        Spacer(modifier = Modifier.height(grid_1))
        Text(
            text = stringResource(R.string.this_will_automatically_save_your_cgpa_to_clear_your_cgpa_tap_the_clear_option_in_option_menu),
            color = MaterialTheme.colorScheme.captionColor,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CgpaEditTextPreview() {
    BITAppTheme {

    }
}

