/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/1/21, 1:09 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/1/21, 11:14 AM
 */



package com.atech.core.data.room.holiday

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HolidayDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(holidayCacheEntity: HolidayCacheEntity)

    @Query("SELECT * FROM holiday_table WHERE type LIKE '%'||:query||'%'")
    fun get(query: String): Flow<List<HolidayCacheEntity>>

    @Query("SELECT * FROM HOLIDAY_TABLE WHERE month LIKE '%'||:month||'%'")
    fun getMonth(month: String): Flow<List<HolidayCacheEntity>>


    @Query("SELECT * FROM holiday_table WHERE DAY LIKE '%'||:query||'%' OR DATE LIKE '%'||:query||'%' OR occasion LIKE '%'||:query||'%' OR month LIKE '%'||:query||'%'")
    suspend fun getSearchHoliday(query: String): List<HolidayCacheEntity>

    @Query("DELETE FROM holiday_table")
    suspend fun deleteAll()
}
