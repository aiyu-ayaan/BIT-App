package com.atech.chat.compose

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.atech.chat.ChatViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val chatUiState by viewModel.uiState
    val isLoading by viewModel.isLoading
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Chat") },
            )
        },
        bottomBar = {
            MessageInput(
                onSendMessage = { inputText ->
                    viewModel.sendMessage(inputText)
                },
                resetScroll = {
                    coroutineScope.launch {
                        listState.scrollToItem(0)
                    }
                },
                isLoading = isLoading
            )
        }) { paddingValues ->
        ChatList(
            chatMessages = chatUiState.messages,
            listState = listState,
            modifier = Modifier.consumeWindowInsets(paddingValues),
            contentPadding = paddingValues
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun ChatViewModelPreview() {
    MaterialTheme {
//        ChatMessageItem()
    }
}