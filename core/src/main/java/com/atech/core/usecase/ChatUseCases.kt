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
    val dao: ChatDao
) {
    suspend operator fun invoke(chat: ChatModel) = dao.insert(chat)

}

data class DeleteChat @Inject constructor(
    val dao: ChatDao
) {
    suspend operator fun invoke(chat: ChatModel) {
        dao.deleteFromId(chat.id)
        dao.deleteFromId(chat.linkId)
    }
}

data class DeleteAllChat @Inject constructor(
    val dao: ChatDao
) {
    suspend operator fun invoke() {
        dao.deleteAll()
    }
}

data class GetAllChat @Inject constructor(
    val dao: ChatDao
) {
    suspend operator fun invoke(): List<ChatModel> {
        return dao.getAll()
    }
}

data class AutoDeleteChatIn30Days @Inject constructor(
    val dao: ChatDao
) {
    suspend operator fun invoke() {
        val list = dao.getAll()
        if (list.isEmpty()) return
        val current = System.currentTimeMillis()
        val thirtyDaysInMillis = 30 * 24 * 60 * 60 * 1000L
        list.filter { chat ->
            current - chat.created > thirtyDaysInMillis
        }.forEach { chatModel ->
            dao.delete(chatModel)
        }
    }
}