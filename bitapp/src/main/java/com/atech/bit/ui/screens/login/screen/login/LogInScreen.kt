package com.atech.bit.ui.screens.login.screen.login

import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atech.bit.R
import com.atech.bit.ui.activity.main.MainViewModel
import com.atech.bit.ui.activity.main.ThemeMode
import com.atech.bit.ui.comman.GoogleButton
import com.atech.bit.ui.navigation.LogInRoutes
import com.atech.bit.ui.navigation.TopLevelRoute
import com.atech.bit.ui.screens.login.LogInScreenEvents
import com.atech.bit.ui.screens.login.LogInState
import com.atech.bit.ui.screens.login.LogInViewModel
import com.atech.bit.ui.screens.login.util.GoogleAuthUiClient
import com.atech.bit.ui.theme.AppLogo
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.ui.theme.image_view_log_in_size
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: LogInViewModel = hiltViewModel(),
    communicatorViewModel: MainViewModel = hiltViewModel()
) {
    var isDialogVisible by rememberSaveable { mutableStateOf(false) }
    var logInMessage by rememberSaveable { mutableStateOf("Creating Account...") }
    var hasClick by rememberSaveable { mutableStateOf(false) }
    val logInState by viewModel.logInState
    val context = LocalContext.current
    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = context, oneTapClient = Identity.getSignInClient(context)
        )
    }
    LaunchedEffect(key1 = true) {
        if (viewModel.logInUseCase.hasLogIn() && !viewModel.hasSetUpDone) {
            navigateToChooseSem(navController)
        }
        if (viewModel.logInUseCase.hasLogIn() && viewModel.hasSetUpDone) {
            navController.navigate(
                TopLevelRoute.MAIN_SCREEN.route
            ) {
                popUpTo(TopLevelRoute.LOGIN.route) {
                    inclusive = true
                }
            }
        }
    }

    LaunchedEffect(key1 = logInState.errorMessage) {
        logInState.errorMessage?.let { error ->
            hasClick = false
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }
    LaunchedEffect(key1 = logInState.userToken) {
        logInState.userToken?.let { token ->
            logInMessage = "Signing In... 🔃"
            viewModel.logInUseCase.logIn(token).let { (hasData, ex) ->
                if (ex != null) {
                    viewModel.onEvent(
                        LogInScreenEvents.OnSignInResult(
                            logInState.copy(
                                errorMessage = ex.message ?: "Unknown Error",
                            )
                        )
                    )
                } else {
                    if (!hasData) {
                        logInMessage = "Welcome ❤️ !!"
                        delay(500)
                        navigateToChooseSem(navController)
                    } else {
                        logInMessage = "Setting things up 🏗️!!"
                        viewModel.logInUseCase.performRestore.invoke(
                            viewModel.logInUseCase.getUid.invoke()!!
                        ) {
                            viewModel.updateSetUpDone(true)
                            communicatorViewModel.onEvent(
                                MainViewModel.SharedEvents.FetchUserDetails
                            )
                            navController.navigate(
                                TopLevelRoute.MAIN_SCREEN.route
                            ) {
                                popUpTo(TopLevelRoute.LOGIN.route) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                coroutineScope.launch {
                    val signInResult = googleAuthUiClient.signInWithIntent(
                        data = result.data ?: return@launch
                    )
                    viewModel.onEvent(
                        LogInScreenEvents.OnSignInResult(
                            LogInState(
                                userToken = signInResult.first,
                                errorMessage = signInResult.second?.message
                            )
                        )
                    )
                }
            }
        })

    BITAppTheme(
        statusBarColor = AppLogo,
        dynamicColor = communicatorViewModel.themeState.value.isDynamicColorActive,
        darkTheme = when (communicatorViewModel.themeState.value.isDarkTheme) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
        }
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(image_view_log_in_size)
                        .background(AppLogo)
                ) {
                    Image(
                        modifier = Modifier.align(Alignment.Center),
                        painter = painterResource(id = R.drawable.ic_ayaan_beta),
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.height(grid_1))
                Text(
                    text = "Welcome to BIT App",
                    style = androidx.compose.material.MaterialTheme.typography.h5,
                )
                Spacer(modifier = Modifier.height(grid_1))
                Text(
                    text = "Create an account to save all setting \nand attendance data.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GoogleButton(loadingText = logInMessage, hasClick = hasClick, onClicked = {
                    coroutineScope.launch {
                        val sigInIntentSender = googleAuthUiClient.signIn()
                        launcher.launch(
                            IntentSenderRequest.Builder(
                                sigInIntentSender ?: return@launch
                            ).build()
                        )
                    }
                })
                Spacer(modifier = Modifier.height(grid_1))
                TextButton(onClick = {
                    navigateToChooseSem(navController)
                }) {
                    Text(
                        text = stringResource(R.string.skip), modifier = Modifier.padding(grid_1)
                    )
                }
                Spacer(modifier = Modifier.height(grid_1))
                Text(
                    text = stringResource(R.string.why_would_you_log_in),
                    style = androidx.compose.material.MaterialTheme.typography.caption,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(grid_1)
                        .padding(start = grid_1)
                        .clickable {
                            isDialogVisible = true
                        },
                    textDecoration = TextDecoration.Underline
                )
                Spacer(modifier = Modifier.height(grid_1))
            }
            if (isDialogVisible) WhyLogIn(onDismissRequest = {
                isDialogVisible = false
            })
        }
    }
}

private fun navigateToChooseSem(navController: NavController) {
    navController.navigate(
        LogInRoutes.SetupScreen.route
    ) {
        popUpTo(LogInRoutes.LogInScreen.route) {
            inclusive = true
        }
    }
}

@Composable
fun WhyLogIn(
    modifier: Modifier = Modifier, onDismissRequest: () -> Unit = {}
) {
    AlertDialog(modifier = modifier,
        onDismissRequest = { onDismissRequest.invoke() },
        confirmButton = {
            TextButton(onClick = { onDismissRequest.invoke() }) {
                Text(text = "Ok")
            }
        },
        icon = {
            Icon(imageVector = Icons.Outlined.AccountTree, contentDescription = null)
        },
        title = {
            Text(text = "Why do you need to log in?")
        },
        text = {
            Text(
                text = """
                        You need to log in to save your data (e.g. Attendance, GPA, Course preferences) in the cloud.
                        
                        This way you can access your data from any device.
                    """.trimIndent()
            )
        })
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    BITAppTheme {
        LoginScreen()
    }
}