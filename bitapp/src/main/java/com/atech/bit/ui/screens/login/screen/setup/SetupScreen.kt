package com.atech.bit.ui.screens.login.screen.setup

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowUp
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.atech.bit.R
import com.atech.bit.ui.activity.main.MainViewModel
import com.atech.bit.ui.activity.main.ThemeMode
import com.atech.bit.ui.comman.ChooseSemBottomSheet
import com.atech.bit.ui.comman.LottieAnim
import com.atech.bit.ui.navigation.TopLevelRoute
import com.atech.bit.ui.screens.login.LogInScreenEvents
import com.atech.bit.ui.screens.login.LogInViewModel
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.ui.theme.grid_2
import com.atech.bit.utils.getVersion
import com.atech.core.utils.UpdateDataType
import kotlinx.coroutines.launch

@Composable
fun SetUpScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    communicatorViewModel: MainViewModel = hiltViewModel(),
    viewModel: LogInViewModel = hiltViewModel()
) {
    val courseDetail by communicatorViewModel.courseDetail
    val course by viewModel.course
    val sem by viewModel.sem
    var isBottomSheetVisible by rememberSaveable {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    BITAppTheme(
        statusBarColor = null,
        dynamicColor = communicatorViewModel.themeState.value.isDynamicColorActive,
        darkTheme = when (communicatorViewModel.themeState.value.isDarkTheme) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LottieAnim(
                    res = R.raw.welcome,
                    modifier = Modifier.size(400.dp)
                )
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(grid_1))
                Text(
                    text = stringResource(R.string.version, getVersion()),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .padding(grid_2)
                    .align(Alignment.BottomEnd),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                text = { Text(text = "Course Preference") },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardDoubleArrowUp,
                        contentDescription = null
                    )
                },
                onClick = {
                    isBottomSheetVisible = true
                })
            if (isBottomSheetVisible) {
                ChooseSemBottomSheet(
                    model = courseDetail,
                    course = course,
                    sem = sem,
                    onDismissRequest = {
                        isBottomSheetVisible = false
                    },
                    onSaveClick = {
                        isBottomSheetVisible = false
                        viewModel.onEvent(
                            LogInScreenEvents.SaveCoursePref(
                                it.first,
                                it.second
                            )
                        )
                        if (viewModel.logInUseCase.hasLogIn()) {
                            scope.launch {
                                viewModel.logInUseCase.uploadData.invoke(
                                    UpdateDataType
                                        .UpdateCourseSem(
                                            course = it.first,
                                            sem = it.second
                                        )
                                )
                            }
                            viewModel.updateSetUpDone(true)
                        }
                        navController.navigate(
                            TopLevelRoute.MAIN_SCREEN.route
                        ) {
                            popUpTo(TopLevelRoute.LOGIN.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SetUpScreenPreview() {
    BITAppTheme {
        SetUpScreen()
    }
}
