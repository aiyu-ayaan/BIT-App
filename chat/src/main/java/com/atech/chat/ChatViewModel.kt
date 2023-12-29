package com.atech.chat

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.core.usecase.ChatUseCases
import com.atech.core.utils.TAGS
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.PromptBlockedException
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val generativeModel: GenerativeModel,
    private val case: ChatUseCases,
    private val mapper: ChatMessageToModelMapper
) : ViewModel() {

    private var chat: Chat? = null

    private val _uiState: MutableState<ChatUiState> = mutableStateOf(ChatUiState())
    val uiState: State<ChatUiState> = _uiState

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading
    private var job: Job? = null

    init {
        getChat()
    }

    private fun getChat() = viewModelScope.launch {
        val history = mapper.mapFromEntityList(case.getAllChat.invoke()).toContent()
        chat = generativeModel.startChat(history)
        _uiState.value = ChatUiState(chat?.history?.map { content ->
            // Map the initial messages
            ChatMessage(
                text = content.parts.first().asTextOrNull() ?: "",
                participant = if (content.role == "user") Participant.USER else Participant.MODEL,
            )
        } ?: emptyList())
    }

    fun onEvent(event: ChatScreenEvents) {
        when (event) {
            is ChatScreenEvents.OnNewMessage -> sendMessage(event.message)
            ChatScreenEvents.OnCancelClick -> cancelJob()
            is ChatScreenEvents.OnChatHistoryDelete -> TODO()
            ChatScreenEvents.OnDeleteAllClick -> viewModelScope.launch {
                case.deleteAllChat.invoke()
                _uiState.value = ChatUiState()
            }
        }
    }


    private fun cancelJob() {
        try {
            _isLoading.value = false
            job?.cancel()
        } catch (e: Exception) {
            Log.e(TAGS.BIT_ERROR.name, "cancelJob: ${e.localizedMessage}")
        }
    }

    private fun sendMessage(userMessage: String) {
        // Add a pending message
        val userInput = ChatMessage(
            text = userMessage, participant = Participant.USER,
        )
        _isLoading.value = true
        _uiState.value.addMessage(
            userInput
        )
        job = viewModelScope.launch {
            try {
                val response = chat!!.sendMessage(userMessage)
                response.text?.let { modelResponse ->
                    val modelRes = ChatMessage(
                        text = modelResponse, participant = Participant.MODEL
                    )
                    mapper.mapToEntityList(
                        listOf(
                            _uiState.value.getLastMessage()!!.copy(
                                linkedId = modelRes.id
                            ), modelRes.copy(
                                linkedId = _uiState.value.getLastMessage()!!.id
                            )
                        )
                    ).forEach {
                        case.insertChat.invoke(it)
                    }
                    _uiState.value.addMessage(
                        modelRes
                    )
                }
            } catch (e: Exception) {
                Log.d("AAA", "sendMessage: $e")
                if (e is PromptBlockedException) {
                    _uiState.value.addMessage(
                        ChatMessage(
                            text = "The input you provided contains offensive language, which goes against our community guidelines " + "and standards. Please refrain from using inappropriate language and ensure that your input is " + "respectful and adheres to our guidelines. If you have any questions or concerns, feel free " + "to contact our support team.",
                            participant = Participant.ERROR
                        )
                    )
                    return@launch
                }
                _uiState.value.addMessage(
                    ChatMessage(
                        text = e.localizedMessage ?: "Unknown error",
                        participant = Participant.ERROR
                    )
                )
            } finally {
                _isLoading.value = false
                job = null
            }
        }
    }

}