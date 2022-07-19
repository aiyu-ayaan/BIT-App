/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/1/21, 1:09 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/1/21, 11:14 AM
 */



package com.atech.core.data.room.event

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    /**
     * Insert
     *
     * @author Ayaan
     * @version 4.0.1
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: EventCachedEntity)

    /**
     * List all events
     *
     * @author Ayaan
     * @version 4.0.1
     */
    @Query("SELECT * FROM event_table ORDER BY created DESC")
    fun getEvents(): kotlinx.coroutines.flow.Flow<List<EventCachedEntity>>

    /**
     * Search
     *
     * @author Ayaan
     * @version 4.0.1
     */
    @Query("SELECT * FROM event_table WHERE event_title LIKE '%'||:query||'%' ORDER BY created DESC")
    suspend fun getSearchEvent(query: String): List<EventCachedEntity>

    /**
     * Delete All
     *
     * @author Ayaan
     * @version 4.0.1
     */
    @Query("DELETE FROM event_table")
    suspend fun deleteAll()

    /**
     * Give Event with certain condition
     *
     * @author Ayaan
     * @version 4.0.3
     */
    @Query("SELECT * FROM event_table WHERE created BETWEEN :start AND :end")
    fun getEvents7Days(start: Long, end: Long): Flow<List<EventCachedEntity>>

}