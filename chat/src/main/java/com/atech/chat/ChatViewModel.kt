package com.atech.chat

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.chat.utils.getChatSetting
import com.atech.chat.utils.saveChatSetting
import com.atech.core.usecase.ChatUseCases
import com.atech.core.utils.TAGS
import com.atech.core.utils.connectivity.ConnectivityObserver
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.PromptBlockedException
import com.google.ai.client.generativeai.type.asTextOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val generativeModel: GenerativeModel,
    private val case: ChatUseCases,
    private val mapper: ChatMessageToModelMapper,
    connectivityObserver: ConnectivityObserver,
    private val pref: SharedPreferences,
) : ViewModel() {

    val connectivity = connectivityObserver.observe()
    private var chat: Chat? = null

    private val _uiState: MutableState<ChatUiState> = mutableStateOf(ChatUiState())
    val uiState: State<ChatUiState> = _uiState

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading
    private var chatJob: Job? = null
    private var responseJob: Job? = null

    private val _chatSettingUi = mutableStateOf(getChatSetting(pref))
    val chatSettingUi: State<ChatSettingUiState> get() = _chatSettingUi

    private var incrementChance: () -> Unit = {}

    fun setAction(action: () -> Unit) {
        this.incrementChance = action
    }

    init {
        getChat()
    }

    private fun getChat() {
        chatJob?.cancel()
        chatJob =
            viewModelScope.launch {
                val fromDatabase = mapper.mapFromEntityList(case.getAllChat.invoke())
                val history = fromDatabase.toContent()
                chat = generativeModel.startChat(history)
                _uiState.value = ChatUiState(chat?.history?.mapIndexed { index, content ->
                    // Map the initial messages
                    ChatMessage(
                        text = content.parts.first().asTextOrNull() ?: "",
                        participant = if (content.role == "user") Participant.USER else Participant.MODEL,
                        id = fromDatabase[index].id,
                        linkedId = fromDatabase[index].linkedId
                    )
                } ?: emptyList())
            }
    }

    fun onEvent(event: ChatScreenEvents) {
        when (event) {
            is ChatScreenEvents.OnNewMessage -> sendMessage(event.message)
            ChatScreenEvents.OnCancelClick -> {
                cancelJob()
            }

            is ChatScreenEvents.OnChatDelete -> viewModelScope.launch {
                if (_chatSettingUi.value.isKeepChat)
                    case.deleteChat.invoke(mapper.mapFormEntity(event.model))
                getChat()
            }

            ChatScreenEvents.OnDeleteAllClick -> viewModelScope.launch {
                case.deleteAllChat.invoke()
                _uiState.value = ChatUiState()
            }
        }
    }


    private fun cancelJob() {
        try {
            _isLoading.value = false
            responseJob?.cancel()
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
        responseJob = viewModelScope.launch {
            try {
                val response = chat!!.sendMessage(userMessage)
                response.text?.let { modelResponse ->
                    val modelRes = ChatMessage(
                        text = modelResponse, participant = Participant.MODEL
                    )
                    if (_chatSettingUi.value.isKeepChat)
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
                    incrementChance.invoke()
                }
                if (_chatSettingUi.value.isKeepChat)
                    getChat()
            } catch (e: Exception) {
                if (e is PromptBlockedException) {
                    _uiState.value.addMessage(
                        ChatMessage(
                            text = "The input you provided contains offensive language, which goes against our community guidelines "
                                    + "and standards. Please refrain from using inappropriate language and ensure that your input is "
                                    + "respectful and adheres to our guidelines. If you have any questions or concerns, feel free "
                                    + "to contact our support team.",
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
                responseJob = null
            }
        }
    }

    fun onChatSettingEvents(event: ChatSettingsEvent) {
        when (event) {
            ChatSettingsEvent.KeepChat -> {
                _chatSettingUi.value =
                    _chatSettingUi.value.copy(isKeepChat = !_chatSettingUi.value.isKeepChat)
                saveChatSetting(
                    pref,
                    _chatSettingUi.value,
                )
            }

            ChatSettingsEvent.AutoDeleteChat -> {
                _chatSettingUi.value =
                    _chatSettingUi.value.copy(isAutoDeleteChat = !_chatSettingUi.value.isAutoDeleteChat)
                saveChatSetting(
                    pref,
                    _chatSettingUi.value,
                )
            }

            ChatSettingsEvent.WrapWord -> {
                _chatSettingUi.value =
                    _chatSettingUi.value.copy(isWrapWord = !_chatSettingUi.value.isWrapWord)
                saveChatSetting(
                    pref,
                    _chatSettingUi.value,
                )
            }
        }
    }
}