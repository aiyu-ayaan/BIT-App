package com.atech.bit.ui.screens.cgpa.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atech.bit.R
import com.atech.bit.ui.comman.BackToolbar
import com.atech.bit.ui.screens.cgpa.CGPAEvent
import com.atech.bit.ui.screens.cgpa.CgpaViewModel
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.ui.theme.grid_2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CgpaScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: CgpaViewModel = hiltViewModel()
) {
    val sem /*by viewModel.savedSem*/ = 6
    val course by viewModel.savedCourse
    val sem1 by viewModel.sem1
    val sem2 by viewModel.sem2
    val sem3 by viewModel.sem3
    val sem4 by viewModel.sem4
    val sem5 by viewModel.sem5
    val sem6 by viewModel.sem6
    val hasError by viewModel.hasError
    val calculateCGPA by viewModel.savedCalculateCGPA

    val rememberScrollState = rememberScrollState()
    val topBarScrollState = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()


    Scaffold(
        modifier = modifier
            .nestedScroll(topBarScrollState.nestedScrollConnection),
        topBar = {
            BackToolbar(
                title = R.string.cgpa_calculator, onNavigationClick = {
                    navController.navigateUp()
                },
                scrollBehavior = topBarScrollState
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(grid_1)
                .verticalScroll(rememberScrollState),
        ) {
            CgpaTitle(
                upToText = "$course up-to sem$sem"
            )
            Spacer(modifier = Modifier.height(grid_2))
            CgpaEditText(
                model = sem1,
                onValueChange = { model ->
                    viewModel.onEvent(
                        CGPAEvent.OnSem1Change(model)
                    )
                }
            )
            if (sem >= 2)
                CgpaEditText(
                    model = sem2,
                    onValueChange = { model ->
                        viewModel.onEvent(
                            CGPAEvent.OnSem2Change(model)
                        )
                    }
                )
            if (sem >= 3)
                CgpaEditText(
                    model = sem3,
                    onValueChange = { model ->
                        viewModel.onEvent(
                            CGPAEvent.OnSem3Change(model)
                        )
                    }
                )
            if (sem >= 4)
                CgpaEditText(
                    model = sem4,
                    onValueChange = { model ->
                        viewModel.onEvent(
                            CGPAEvent.OnSem4Change(model)
                        )
                    }
                )
            if (sem >= 5)
                CgpaEditText(
                    model = sem5,
                    onValueChange = { model ->
                        viewModel.onEvent(
                            CGPAEvent.OnSem5Change(model)
                        )
                    }
                )
            if (sem >= 6)
                CgpaEditText(
                    model = sem6,
                    onValueChange = { model ->
                        viewModel.onEvent(
                            CGPAEvent.OnSem6Change(model)
                        )
                    }
                )
            CgpaFooter(
                value = if (calculateCGPA == 1.0) " " else calculateCGPA.toString(),
                enable = !hasError,
                onCalculate = {
                    viewModel.onEvent(CGPAEvent.CalculateAndSave)
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun CgpaScreenPreview() {
    BITAppTheme {
        CgpaScreen()
    }
}