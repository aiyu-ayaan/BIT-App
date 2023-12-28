package com.atech.core.usecase

import com.atech.core.datasource.room.chat.ChatDao
import com.atech.core.datasource.room.chat.ChatModel
import javax.inject.Inject

data class ChatUseCases @Inject constructor(
    val insertChat: InsertChat,
    val deleteChat: DeleteChat,
    val deleteAllChat: DeleteAllChat,
    val getAllChat: GetAllChat
)

data class InsertChat @Inject constructor(
    val chatRepository: ChatDao
) {
    suspend operator fun invoke(chat: ChatModel) =
        chatRepository.insert(chat)

}

data class DeleteChat @Inject constructor(
    val chatRepository: ChatDao
) {
    suspend operator fun invoke(chat: ChatModel) {
        chatRepository.delete(chat)
    }
}

data class DeleteAllChat @Inject constructor(
    val chatRepository: ChatDao
) {
    suspend operator fun invoke() {
        chatRepository.deleteAll()
    }
}

data class GetAllChat @Inject constructor(
    val chatRepository: ChatDao
) {
    suspend operator fun invoke(): List<ChatModel> {
        return chatRepository.getAll()
    }
}