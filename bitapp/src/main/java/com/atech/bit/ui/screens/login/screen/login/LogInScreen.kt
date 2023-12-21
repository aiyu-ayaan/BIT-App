package com.atech.bit.ui.screens.login.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atech.bit.R
import com.atech.bit.ui.comman.GoogleButton
import com.atech.bit.ui.theme.AppLogo
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.ui.theme.image_view_log_in_size

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
) {
    var isDialogVisible by rememberSaveable { mutableStateOf(false) }
    BITAppTheme(
        statusBarColor = AppLogo
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
                GoogleButton(onClicked = {})
                Spacer(modifier = Modifier.height(grid_1))
                TextButton(onClick = { /*TODO*/ }) {
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
            if (isDialogVisible)
                WhyLogIn(
                    onDismissRequest = {
                        isDialogVisible = false
                    }
                )
        }
    }
}

@Composable
fun WhyLogIn(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {}
) {
    AlertDialog(
        modifier = modifier,
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
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    BITAppTheme {
        LoginScreen()
    }
}