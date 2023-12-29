package com.atech.chat.compose

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.atech.chat.ChatMessage
import com.atech.chat.ChatScreenEvents
import com.atech.chat.ChatViewModel
import com.atech.chat.R
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val chatUiState by viewModel.uiState
    val isLoading by viewModel.isLoading
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var isDeleteAllDialogVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var isDeleteChatDialogVisible by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = chatUiState.messages, key2 = isLoading) {
        coroutineScope.launch {
            listState.scrollToItem(
                listState.layoutInfo.totalItemsCount
            )
        }
    }
    var selectedChat: ChatMessage? by remember {
        mutableStateOf(null)
    }

    Scaffold(modifier = Modifier, topBar = {
        TopAppBar(title = {
            Text(
                text = stringResource(R.string.tutortalk), color = MaterialTheme.colorScheme.primary
            )
        }, navigationIcon = {
            IconButton(
                onClick = {
                    navController.popBackStack()
                },
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }, actions = {
            if (chatUiState.messages.isNotEmpty()) {
                IconButton(onClick = {
                    isDeleteAllDialogVisible = true
                }) {
                    Icon(
                        imageVector = Icons.Outlined.ClearAll,
                        contentDescription = "Delete All",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        })
    }, bottomBar = {
        MessageInput(onSendMessage = { inputText ->
            viewModel.onEvent(
                ChatScreenEvents.OnNewMessage(
                    inputText
                )
            )
        }, resetScroll = {
            coroutineScope.launch {
                listState.scrollToItem(
                    listState.layoutInfo.totalItemsCount
                )
            }
        }, isLoading = isLoading, onCancelClick = {
            ChatScreenEvents.OnCancelClick
        })
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            if (chatUiState.messages.isEmpty()) {
                EmptyScreen(modifier = Modifier.padding(paddingValues))
                return@Scaffold
            }
            ChatList(
                chatMessages = chatUiState.messages,
                listState = listState,
                modifier = Modifier,
            ) {
                selectedChat = it
                isDeleteChatDialogVisible = true
            }
        }
    }
    if (isDeleteAllDialogVisible) {
        ChatDialog(title = stringResource(R.string.delete_all_chats),
            text = stringResource(R.string.are_you_sure_you_want_to_delete_all_chats),
            icon = Icons.Outlined.Chat,
            onDismissRequest = { isDeleteAllDialogVisible = false },
            onConfirm = {
                viewModel.onEvent(
                    ChatScreenEvents.OnDeleteAllClick
                )
            })
    }
    if (isDeleteChatDialogVisible) {
        ChatDialog(title = stringResource(R.string.delete_chat),
            text = stringResource(R.string.are_you_sure_delete_single_chat),
            icon = Icons.Outlined.Chat,
            onDismissRequest = {
                isDeleteChatDialogVisible = false
                selectedChat = null
            },
            onConfirm = {
                Log.d("AAA", "ChatScreen: ${selectedChat?.id}")
                viewModel.onEvent(
                    ChatScreenEvents.OnChatDelete(selectedChat ?: return@ChatDialog)
                )
            })
    }
}

@Composable
fun EmptyScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = modifier.background(
                MaterialTheme.colorScheme.primary.copy(alpha = .3f), shape = CardDefaults.shape
            ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Icon(
                modifier = Modifier.size(38.dp),
                imageVector = Icons.Outlined.Chat,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            MarkdownText(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                markdown = stringResource(R.string.intro_text),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

    }
}

@Composable
fun ChatDialog(
    modifier: Modifier = Modifier,
    title: String,
    text: String,
    icon: ImageVector,
    onDismissRequest: () -> Unit,
    confirmMessage: String = stringResource(R.string.yes),
    onConfirm: () -> Unit,
    dismissMessage: String = stringResource(R.string.no),
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onConfirm.invoke()
                onDismissRequest.invoke()
            }) {
                Text(text = confirmMessage)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = dismissMessage)
            }
        },
        icon = {
            Icon(imageVector = icon, contentDescription = null)
        },
        text = {
            Text(text = text)
        },
        title = {
            Text(text = title)
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun ChatViewModelPreview() {
    MaterialTheme {
        EmptyScreen()
    }
}