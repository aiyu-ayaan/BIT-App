package com.atech.core.datasource.room.chat

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat order by created")
    suspend fun getAll(): List<ChatModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chat: ChatModel)

    @Query("DELETE FROM chat")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(chat: ChatModel)
}