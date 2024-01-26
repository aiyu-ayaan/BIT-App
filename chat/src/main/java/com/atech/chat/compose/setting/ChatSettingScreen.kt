/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.chat.compose.setting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoDelete
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.WrapText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atech.chat.ChatSettingUiState
import com.atech.chat.ChatSettingsEvent
import com.atech.chat.comman.MarkDown

const val settingScreenRouteName = "chat_setting"
private val grid_1 = 8.dp
private val grid_2 = 16.dp
private val grid_3 = 24.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatSettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    uiState: ChatSettingUiState,
    onEvent: (ChatSettingsEvent) -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(text = "TutorTalk Settings")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = null,
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
        ) {
            PreferenceItem(
                title = "Keep Chats",
                description = "Save your chats on your device",
                icon = Icons.AutoMirrored.Outlined.Chat,
                onClick = {
                    onEvent(ChatSettingsEvent.KeepChat)
                },
                isChecked = uiState.isKeepChat
            )
            AnimatedVisibility(
                visible = uiState.isKeepChat,
            ) {
                PreferenceItem(
                    title = "Auto Delete Chats",
                    description = "Delete your chats after 30 days",
                    icon = Icons.Outlined.AutoDelete,
                    onClick = {
                        onEvent(ChatSettingsEvent.AutoDeleteChat)
                    },
                    isChecked = uiState.isAutoDeleteChat
                )
            }
            PreferenceItem(
                title = "Word Wrap",
                description = "Wrap long messages to fit your screen",
                icon = Icons.Outlined.WrapText,
                onClick = {
                    onEvent(ChatSettingsEvent.WrapWord)
                },
                isChecked = uiState.isWrapWord
            )
            MarkDown(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(grid_2, grid_1),
                wrapWord = uiState.isWrapWord,
                markDown = """
                ```java
                class BitApp{
                    public static void main(String args[]){
                        System.out.println("This code is only for demo purpose");
                        System.out.println("© BIT App 2021");
                    }
                }
                ```
            """.trimIndent()
            )
        }
    }
}

@Composable
internal fun PreferenceItemDescription(
    modifier: Modifier = Modifier,
    text: String,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    enabled: Boolean,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    Text(
        modifier = modifier.padding(top = 2.dp),
        text = text,
        maxLines = maxLines,
        style = style,
        color = if (enabled) color else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
        overflow = overflow
    )
}

@Composable
internal fun PreferenceItemTitle(
    modifier: Modifier = Modifier,
    text: String,
    maxLines: Int = 2,
    enabled: Boolean,
    color: Color = MaterialTheme.colorScheme.onSurface,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    Text(
        modifier = modifier,
        text = text,
        maxLines = maxLines,
        style = MaterialTheme.typography.titleMedium,
        color = if (enabled) color else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
        overflow = overflow
    )
}

@Composable
fun PreferenceItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isChecked: Boolean = true,
    checkedIcon: ImageVector = Icons.Outlined.Check,
    onClick: (() -> Unit) = {},
) {
    val thumbContent: (@Composable () -> Unit)? = if (isChecked) {
        {
            Icon(
                imageVector = checkedIcon,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        null
    }
    Surface(
        modifier = modifier
            .toggleable(
                value = isChecked, onValueChange = { onClick() }, enabled = enabled
            )
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(grid_2, grid_1)
                .padding(start = if (icon == null) 12.dp else 0.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = grid_1, end = grid_2)
                        .size(grid_3),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                PreferenceItemTitle(
                    text = title, enabled = enabled
                )
                if (!description.isNullOrEmpty()) PreferenceItemDescription(
                    text = description, enabled = enabled
                )
            }
            Switch(
                checked = isChecked,
                onCheckedChange = null,
                modifier = Modifier.padding(start = 20.dp, end = 6.dp),
                enabled = enabled,
                thumbContent = thumbContent
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatSettingsScreenPreview() {
    MaterialTheme {
        ChatSettingsScreen(
            uiState = ChatSettingUiState()
        )
    }
}