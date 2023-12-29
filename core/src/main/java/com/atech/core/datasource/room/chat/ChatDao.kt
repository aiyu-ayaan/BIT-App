package com.atech.core.datasource.room.chat

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_table order by created")
    suspend fun getAll(): List<ChatModel>


    @Query("DELETE FROM chat_table WHERE id = :id")
    suspend fun deleteFromId(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chat: ChatModel)

    @Query("DELETE FROM chat_table")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(chat: ChatModel)
}