/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 2/13/22, 10:50 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 2/13/22, 8:36 PM
 */



package com.atech.core.data_source.room.attendance

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
    suspend fun insertAll(attendance: List<AttendanceModel>)

    @Update
    suspend fun update(attendance: AttendanceModel)

    @Delete
    suspend fun delete(attendance: AttendanceModel)


    @RawQuery(observedEntities = [AttendanceModel::class])
    fun getAttendanceSorted(query: SupportSQLiteQuery): PagingSource<Int, AttendanceModel>


    @Query("SELECT * FROM attendance_table WHERE isArchive is NULL or isArchive = 0")
    fun getNonArchiveAttendance(): Flow<List<AttendanceModel>>


    @Query("SELECT * FROM attendance_table ORDER BY id ASC")
    suspend fun getAllAttendance(): List<AttendanceModel>

    @Query("SELECT * FROM attendance_table ORDER BY id ASC")
    suspend fun getAllAttendanceList(): List<AttendanceModel>

    @Query("SELECT * FROM attendance_table WHERE fromOnlineSyllabus = 1")
    fun getAttendanceAddedFromOnline(): Flow<List<AttendanceModel>>

    @Query("SELECT id from attendance_table where subject_name = :subjectName")
    suspend fun getElementIdFromSubject(subjectName: String): Int?

    @Query("SELECT * FROM attendance_table WHERE isArchive = 1 ORDER BY id ASC")
    fun getAllArchiveAttendance(): Flow<List<AttendanceModel>>

    @Query("SELECT * FROM attendance_table WHERE subject_name LIKE '%'||:query||'%'")
    suspend fun getAttendance(query: String): AttendanceModel

    @Query("DELETE FROM attendance_table")
    suspend fun deleteAll()


    @Query("DELETE FROM attendance_table WHERE subject_name = :name")
    suspend fun deleteFromSubjectName(name: String)


    /**
     * @since 4.0.3
     * @author Ayaan
     */
    @Query("SELECT * FROM attendance_table WHERE subject_name = :query")
    suspend fun findSyllabus(query: String): AttendanceModel?


    @Query("DELETE FROM attendance_table WHERE isArchive = 1")
    suspend fun deleteAllArchiveAttendance()

    @Query("SELECT * FROM attendance_table WHERE id = :id")
    suspend fun getAttendanceById(id: Int): AttendanceModel?

}