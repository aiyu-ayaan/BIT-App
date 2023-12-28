package com.atech.core.datasource.room.chat

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "chat")
data class ChatModel(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val text: String,
    val participant: String,
    val created: Long = System.currentTimeMillis(),
    val linkId: String = ""
)

