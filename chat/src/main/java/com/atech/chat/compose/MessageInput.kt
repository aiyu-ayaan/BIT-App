package com.atech.chat.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.atech.chat.R
import com.atech.core.utils.connectivity.ConnectivityObserver


val drawerColor: Color
    @Composable
    get() = ColorUtils.blendARGB(
        MaterialTheme.colorScheme.surface.toArgb(),
        MaterialTheme.colorScheme.primary.toArgb(),
        .09f
    ).let { Color(it) }

@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    hasUnlimitedAccess: Boolean = false,
    resetScroll: () -> Unit = {},
    isLoading: Boolean,
    onCancelClick: () -> Unit = {},
    isConnected: ConnectivityObserver.Status =
        ConnectivityObserver.Status.Available,
    current: String = "1/20",
    hasLogIn: Boolean = true
) {
    var userMessage by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp, top = 8.dp)
            .fillMaxWidth(),
    ) {
        val trailingIcon: @Composable (() -> Unit)? =
            if (isLoading) {
                {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        strokeCap = StrokeCap.Round
                    )
                }
            } else if (userMessage.isBlank() && !hasUnlimitedAccess) {
                {
                    Text(text = current)
                }
            } else if (userMessage.isNotBlank()) {
                {
                    IconButton(
                        onClick = {
                            userMessage = ""
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Clear,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else null
        OutlinedTextField(
            value = userMessage,
            placeholder = { Text(stringResource(R.string.message)) },
            onValueChange = { userMessage = it },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
            ),
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .fillMaxWidth()
                .weight(0.85f),
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(32.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = drawerColor,
                unfocusedContainerColor = drawerColor,
            ),
            enabled = hasLogIn
        )
        val isEnable = (userMessage.isNotBlank() || isLoading) &&
                isConnected == ConnectivityObserver.Status.Available
        IconButton(
            enabled = isEnable,
            onClick = {
                if (isLoading) onCancelClick()
                else if (userMessage.isNotBlank()) {
                    onSendMessage(userMessage)
                    userMessage = ""
                    resetScroll()
                    focusManager.clearFocus()
                }
            },
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.CenterVertically)
                .fillMaxWidth()
                .weight(0.15f)
        ) {
            Icon(
                if (isConnected != ConnectivityObserver.Status.Available)
                    Icons.Outlined.WifiOff
                else
                    if (isLoading) Icons.Outlined.PauseCircle
                    else Icons.Outlined.Send,
                contentDescription = "send",
                tint =
                if (isConnected != ConnectivityObserver.Status.Available)
                    MaterialTheme.colorScheme.error
                else
                    if (isEnable) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun MessageInputPreview() {
    MaterialTheme {
        MessageInput(
            onSendMessage = {},
            isLoading = false,
            isConnected = ConnectivityObserver.Status.Available
        )
    }
}