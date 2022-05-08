/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/14/22, 2:16 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/14/22, 11:41 AM
 */

package com.aatec.core.data.room.timeTable

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aatec.core.data.room.timeTable.TimeTableCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeTableDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notice: TimeTableCacheEntity)

    @Query("DELETE FROM time_table_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM time_table_table WHERE course LIKE '%'||:course||'%' and gender LIKE '%'||:gender||'%' and sem LIKE '%'||:sem||'%' and section LIKE '%'||:sec||'%'")
    fun getTimeTableDefault(
        course: String,
        gender: String,
        sem: String,
        sec: String
    ): Flow<TimeTableCacheEntity?>


    @Query("SELECT * FROM TIME_TABLE_TABLE ORDER BY created")
    fun getAll(): Flow<List<TimeTableCacheEntity>>
}