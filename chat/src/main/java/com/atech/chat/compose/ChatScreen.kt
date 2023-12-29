package com.atech.chat.compose

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
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
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

    LaunchedEffect(key1 = chatUiState.messages, key2 = isLoading) {
        coroutineScope.launch {
            listState.scrollToItem(
                listState.layoutInfo.totalItemsCount
            )
        }
    }

    Scaffold(modifier = Modifier, topBar = {
        TopAppBar(title = {
            Text(
                text = stringResource(R.string.tutortalk),
                color = MaterialTheme.colorScheme.primary
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
            IconButton(onClick = {
                viewModel.onEvent(
                    ChatScreenEvents.OnDeleteAllClick
                )
            }) {
                Icon(
                    imageVector = Icons.Outlined.DeleteForever,
                    contentDescription = "Delete All",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        })
    }, bottomBar = {
        MessageInput(
            onSendMessage = { inputText ->
                viewModel.onEvent(
                    ChatScreenEvents.OnNewMessage(
                        inputText
                    )
                )
            },
            resetScroll = {
                coroutineScope.launch {
                    listState.scrollToItem(
                        listState.layoutInfo.totalItemsCount
                    )
                }
            },
            isLoading = isLoading,
            onCancelClick = {
                ChatScreenEvents.OnCancelClick
            }
        )
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
            )
        }
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

@Preview(showBackground = true)
@Composable
private fun ChatViewModelPreview() {
    MaterialTheme {
        EmptyScreen()
    }
}