package com.atech.chat.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.atech.chat.ChatMessage
import com.atech.chat.Participant
import com.atech.core.utils.openLinks
import dev.jeziellago.compose.markdowntext.MarkdownText


@Composable
fun ChatList(
    chatMessages: List<ChatMessage>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        reverseLayout = false,
        state = listState,
    ) {
        items(chatMessages) { message ->
            ChatMessageItem(
                model = message
            )
        }
    }
}

@Composable
fun ChatMessageItem(
    modifier: Modifier = Modifier,
    model: ChatMessage,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .clickable { onClick.invoke() }
            .fillMaxWidth()
    ) {
        val hasError = model.participant == Participant.ERROR
        Column(
            modifier
                .padding(8.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    modifier = Modifier
                        .size(MaterialTheme.typography.titleSmall.fontSize.value.dp),
                    imageVector = when (model.participant) {
                        Participant.MODEL -> Icons.Outlined.Computer
                        Participant.USER -> Icons.Outlined.Person
                        Participant.ERROR -> Icons.Outlined.Error
                    },
                    contentDescription = null,
                    tint = if (hasError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        LocalContentColor.current
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (model.participant) {
                        Participant.MODEL -> "Bot"
                        Participant.USER -> "Me"
                        Participant.ERROR -> "Error"
                    },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .align(Alignment.CenterVertically),
                    style = MaterialTheme.typography.titleSmall,
                    color = if (hasError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        LocalContentColor.current
                    }
                )
            }
            MarkdownText(
                modifier = Modifier
                    .padding(
                        top = 8.dp,
                        start = MaterialTheme.typography.titleSmall.fontSize.value.dp + 8.dp
                    )
                    .align(Alignment.Start),
                markdown = model.text,
                color = if (hasError) {
                    MaterialTheme.colorScheme.error
                } else {
                    LocalContentColor.current
                },
                style = MaterialTheme.typography.bodyMedium,
                onLinkClicked = {
                    it.openLinks(
                        context
                    )
                },
                linkColor = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Start,
                imageLoader = ImageLoader(context)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ChatMessagePreview() {
    MaterialTheme {
        ChatList(
            chatMessages = listOf(
                ChatMessage(
                    text = "Hello world",
                    participant = Participant.MODEL
                ),
                ChatMessage(
                    text = "Hello world",
                    participant = Participant.USER
                )
            ), listState = rememberLazyListState()
        )
    }
}