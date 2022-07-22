package com.atech.core.data.room.events

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventsCacheEntity)


    @Query("SELECT * FROM events_table ORDER BY created DESC")
    fun getEvents(): Flow<List<EventsCacheEntity>>


    @Query("SELECT * FROM events_table WHERE created BETWEEN :start AND :end")
    fun getEvents7Days(start: Long, end: Long): Flow<List<EventsCacheEntity>>

    @Query("SELECT * FROM events_table WHERE title LIKE '%'||:query||'%' ORDER BY created DESC")
    suspend fun getSearchEvent(query: String): List<EventsCacheEntity>

    @Query("DELETE FROM events_table")
    suspend fun deleteAll()
}