package com.atech.core.datasource.room.chat

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "chat")
data class ChatModel(
    val id: String,
    val text: String,
    val participant: String,
    @PrimaryKey(autoGenerate = false)
    val created: Long = System.currentTimeMillis()
)

