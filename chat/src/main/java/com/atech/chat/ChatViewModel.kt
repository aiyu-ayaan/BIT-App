package com.atech.chat

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.core.usecase.ChatUseCases
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    generativeModel: GenerativeModel,
    private val case : ChatUseCases
) : ViewModel() {
    private val chat =
        generativeModel.startChat(
            history = list()
        )

    private fun list(): List<Content> = listOf(
        content(role = "user") { text("Hey") },
        content(role = "model") {
            text(
                "\uD83D\uDC4B Hey! I'm your friendly Chatbot, powered by Gemini Pro! \uD83D\uDE80 Feel free to ask " +
                        "me anything about your course, dive into the functions, or explore the amazing features available." +
                        "Whether it's information," +
                        " assistance, or just a friendly chat, I'm here to help! \uD83C\uDF10\uD83D\uDCAC"
            )
        })

    private val _uiState: MutableState<ChatUiState> =
        mutableStateOf(ChatUiState(chat.history.map { content ->
            // Map the initial messages
            ChatMessage(
                text = content.parts.first().asTextOrNull() ?: "",
                participant = if (content.role == "user") Participant.USER else Participant.MODEL,
                isPending = false
            )
        }))
    val uiState: State<ChatUiState> = _uiState


    fun sendMessage(userMessage: String) {
        // Add a pending message
        _uiState.value.addMessage(
            ChatMessage(
                text = userMessage, participant = Participant.USER, isPending = true
            )
        )

        viewModelScope.launch {
            try {
                val response = chat.sendMessage(userMessage)
                _uiState.value.replaceLastPendingMessage()

                response.text?.let { modelResponse ->
                    _uiState.value.addMessage(
                        ChatMessage(
                            text = modelResponse, participant = Participant.MODEL, isPending = false
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.value.replaceLastPendingMessage()
                _uiState.value.addMessage(
                    ChatMessage(
                        text = e.localizedMessage ?: "Unknown error",
                        participant = Participant.ERROR
                    )
                )
            }
        }
    }
}