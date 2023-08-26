/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 2/13/22, 10:50 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 2/13/22, 8:36 PM
 */



package com.atech.core.room.attendance

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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


    fun getAttendanceSorted(sort: Sort = Sort()): Flow<List<AttendanceModel>> {
        val sortBy = when (sort.sortBy) {
            SortBy.PERCENTAGE -> getNonArchiveAttendanceOrderByPercentage()
            else -> getNonArchiveAttendance().map { attendanceList->
                when(sort.sortBy) {
                    SortBy.SUBJECT -> attendanceList.sortedBy { it.subject }
                    SortBy.CREATED -> attendanceList.sortedBy { it.created }
                    SortBy.TOTAL -> attendanceList.sortedBy { it.total }
                    SortBy.PRESENT -> attendanceList.sortedBy { it.present }
                    else -> attendanceList
                }
            }
        }
        val sortOrder = when (sort.sortOrder) {
            SortOrder.DESC -> sortBy.map { it.reversed() }
            else -> sortBy
        }
        return sortOrder
    }



    @Query("SELECT * FROM attendance_table WHERE isArchive is NULL or isArchive = 0")
    fun getNonArchiveAttendance(): Flow<List<AttendanceModel>>

    @Query("SELECT *,  (CAST(present AS REAL) / total) * 100 AS percentage FROM attendance_table WHERE isArchive is NULL or isArchive = 0 ORDER BY percentage ASC")
    fun getNonArchiveAttendanceOrderByPercentage(): Flow<List<AttendanceModel>>

    @Query("SELECT * FROM attendance_table ORDER BY id ASC")
    fun getAllAttendance(): Flow<List<AttendanceModel>>

    @Query("SELECT * FROM attendance_table ORDER BY id ASC")
    suspend fun getAllAttendanceList(): List<AttendanceModel>

    @Query("SELECT * FROM attendance_table WHERE fromOnlineSyllabus = 1")
    fun getAttendanceAddedFromOnline(): Flow<List<AttendanceModel>>

    @Query("SELECT * FROM attendance_table WHERE subject_name = :subjectName AND fromOnlineSyllabus = 1")
    suspend fun checkSubjectFromOnline(subjectName: String): AttendanceModel?

    @Query("SELECT * FROM attendance_table WHERE isArchive = 1 ORDER BY id ASC")
    fun getAllArchiveAttendance(): Flow<List<AttendanceModel>>

    @Query("SELECT * FROM attendance_table WHERE subject_name LIKE '%'||:query||'%'")
    suspend fun getAttendance(query: String): AttendanceModel

    @Query("DELETE FROM attendance_table")
    suspend fun deleteAll()


    /**
     * @since 4.0.3
     * @author Ayaan
     */
    @Query("SELECT * FROM attendance_table WHERE subject_name = :query")
    suspend fun findSyllabus(query: String): AttendanceModel?


    @Query("DELETE FROM attendance_table WHERE isArchive = 1")
    suspend fun deleteAllArchiveAttendance()
}