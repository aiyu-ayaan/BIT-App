package com.atech.bit.ui.screens.home.compose

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Segment
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import com.atech.bit.R
import com.atech.bit.ui.activity.main.MainViewModel
import com.atech.bit.ui.comman.EditText
import com.atech.bit.ui.comman.ImageIconButton
import com.atech.bit.ui.comman.ImageLoader
import com.atech.bit.ui.screens.login.util.GoogleAuthUiClient
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.dividerOrCardColor
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.ui.theme.grid_2
import com.atech.bit.ui.theme.user_profile_image_size
import com.atech.core.datasource.datastore.Cgpa
import com.atech.core.datasource.firebase.auth.UserData
import com.atech.core.datasource.firebase.auth.UserModel
import com.atech.core.utils.fromJSON
import com.atech.core.utils.getDate
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDialog(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onDismissRequest: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    val userModel by viewModel.useModel
    val userData by viewModel.userData
    val context = LocalContext.current
    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = context, oneTapClient = Identity.getSignInClient(context)
        )
    }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.fetchAccountDetail()
    }
    AlertDialog(
        modifier = modifier
            .fillMaxWidth(),
        onDismissRequest = onDismissRequest,
    ) {
        ProfileDialogCompose(
            userModel = userModel,
            userData = userData,
            onCloseClick = onDismissRequest,
            onDeleteClick = onDeleteClick,
            onSignOutClick = {
                scope.launch {
                    googleAuthUiClient.signOut {
                        viewModel.onEvent(
                            MainViewModel.SharedEvents.PreformSignOut
                        )
                    }
                    onDismissRequest.invoke()
                }
            }
        )
    }
}

@Composable
fun ProfileDialogCompose(
    modifier: Modifier = Modifier,
    userModel: UserModel,
    userData: UserData,
    onCloseClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    var isLogOutDialogVisible by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = AlertDialogDefaults.containerColor,
                shape = AlertDialogDefaults.shape
            )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            ImageIconButton(
                icon = Icons.Outlined.Close,
                onClick = onCloseClick,
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(id = R.string.app_name),
                modifier = Modifier
                    .fillMaxWidth(),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = grid_2,
                    horizontal = grid_1
                ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.dividerOrCardColor
                    .copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(grid_1),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ImageLoader(
                        modifier = Modifier.size(user_profile_image_size),
                        imageUrl = userModel.profilePic,
                        isRounderCorner = true
                    )
                    Spacer(modifier = Modifier.width(grid_2))
                    Column {
                        Text(
                            text = userModel.name ?: "", style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = userModel.email ?: "", style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Spacer(modifier = Modifier.height(grid_1))
                EditText(
                    modifier = Modifier.fillMaxWidth(),
                    value = userData.course,
                    placeholder = stringResource(R.string.course),
                    readOnly = true,
                    trailingIcon = null
                )
                EditText(modifier = Modifier.fillMaxWidth(),
                    value = userData.sem,
                    placeholder = stringResource(R.string.sem),
                    readOnly = true,
                    trailingIcon = null,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Segment,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    })
                val cgpa = if (userData.cgpa != null) fromJSON(
                    userData.cgpa!!,
                    Cgpa::class.java
                )!!.cgpa.let {
                    if (it == 0.0 || it == 1.0) ""
                    else it.toString()
                }
                else ""
                EditText(
                    modifier = Modifier.fillMaxWidth(),
                    value = cgpa,
                    trailingIcon = null,
                    placeholder = stringResource(R.string.cgpa),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Leaderboard,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    readOnly = true
                )
                TextButton(
                    onClick = {
                        isLogOutDialogVisible = true
                    }, modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.sign_out))
                }
                Spacer(modifier = Modifier.height(grid_1))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Sync, contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(grid_1))
                    Text(
                        text = "Last Sync ${userModel.syncTime?.getDate()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(grid_1))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDeleteClick.invoke() },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteForever, contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(grid_1))
                    Text(
                        text = stringResource(R.string.delete_my_account),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
    if (isLogOutDialogVisible) {
        LogOutAlterDialog(
            onDismissRequest = { isLogOutDialogVisible = false },
            onPositiveClick = onSignOutClick
        )
    }
}

@Composable
fun LogOutAlterDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    onPositiveClick: () -> Unit = {},
) {
    AlertDialog(
        modifier = modifier,
        title = {
            Text(text = stringResource(R.string.sign_out))
        },
        text = {
            Text(text = stringResource(R.string.really_want_to_sign_out))
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onPositiveClick.invoke()
                onDismissRequest.invoke()
            }
            ) {
                Text(text = stringResource(R.string.yes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(R.string.no))
            }
        },
    )
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE)
@Preview(showBackground = true)
@Preview(wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE, showBackground = true)
@Composable
private fun ProfileDialogComposePreview() {
    BITAppTheme {
    }
}