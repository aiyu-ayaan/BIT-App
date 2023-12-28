package com.atech.chat.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    resetScroll: () -> Unit = {},
    isLoading: Boolean = true,
    onCancelClick: () -> Unit = {}
) {
    var userMessage by rememberSaveable { mutableStateOf("") }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            val trailingIcon: @Composable (() -> Unit)? = if (isLoading) {
                {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        strokeCap = StrokeCap.Round
                    )
                }
            } else null
            OutlinedTextField(
                value = userMessage,
                label = { Text("Chat") },
                onValueChange = { userMessage = it },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                ),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth()
                    .weight(0.85f),
                trailingIcon = trailingIcon
            )
            val isEnable = userMessage.isNotBlank() || isLoading
            IconButton(
                enabled = isEnable,
                onClick = {
                    if (isLoading) onCancelClick()
                    else if (userMessage.isNotBlank()) {
                        onSendMessage(userMessage)
                        userMessage = ""
                        resetScroll()
                    }
                },
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth()
                    .weight(0.15f)
            ) {
                Icon(
                    if (isLoading) Icons.Outlined.PauseCircle
                    else Icons.Outlined.Send,
                    contentDescription = "send",
                    modifier = Modifier,
                    tint = if (isEnable) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            }
        }
    }
}

@Preview
@Composable
private fun MessageInputPreview() {
    MaterialTheme {
        MessageInput(onSendMessage = {})
    }
}