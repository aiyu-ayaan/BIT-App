/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.core.datasource.room.attendance

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attendance: AttendanceModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(attendances: List<AttendanceModel>)


    @Update
    suspend fun update(attendance: AttendanceModel)

    @Delete
    suspend fun delete(attendance: AttendanceModel)


    @RawQuery(observedEntities = [AttendanceModel::class])
    fun getAttendanceSorted(query: SupportSQLiteQuery): PagingSource<Int, AttendanceModel>

    @Query("SELECT * FROM attendance_table ")
    fun getAttendance(): PagingSource<Int, AttendanceModel>

    @Query("SELECT * FROM attendance_table ORDER BY id ASC")
    suspend fun getAllAttendance(): List<AttendanceModel>


    @Query("SELECT id from attendance_table where subject_name = :subjectName")
    suspend fun getElementIdFromSubject(subjectName: String): Int?

    @Query("SELECT * FROM attendance_table WHERE isArchive = 1 ORDER BY id ASC")
    fun getAllArchiveAttendance(): Flow<List<AttendanceModel>>

    @Query("SELECT * FROM attendance_table WHERE id = :id")
    suspend fun getAttendanceById(id: Int): AttendanceModel?

    @Query("DELETE FROM attendance_table")
    suspend fun deleteALl()

    @Query("DELETE FROM attendance_table WHERE subject_name = :name")
    suspend fun deleteFromSubjectName(name: String)


}