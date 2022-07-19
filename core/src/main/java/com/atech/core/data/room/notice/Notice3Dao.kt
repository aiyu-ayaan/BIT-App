/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 3/17/22, 12:15 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 3/14/22, 8:45 AM
 */

package com.atech.core.data.room.notice

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface Notice3Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notice: Notice3CacheEntity)


    @Query("SELECT * FROM notice_3_table ORDER BY created DESC")
    suspend fun get(): List<Notice3CacheEntity>

    @Query("SELECT * FROM notice_3_table ORDER BY created DESC")
    fun getNoticeFlow(): Flow<List<Notice3CacheEntity>>


    @Query("SELECT * FROM notice_3_table WHERE title LIKE '%'||:query||'%' ORDER BY created DESC")
    suspend fun getNoticeTitle(query: String): List<Notice3CacheEntity>

    @Query("DELETE FROM notice_3_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM notice_3_table WHERE path LIKE '%'||:query||'%'")
    suspend fun getNoticeDeepLink(query: String): Notice3CacheEntity?
}