/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/1/21, 1:09 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/1/21, 11:14 AM
 */



package com.atech.core.room.syllabus

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SyllabusDao {

    /***
     * Get syllabus for search
     */
    @Query("SELECT * FROM syllabus_table WHERE subject LIKE '%'||:query||'%' OR code LIKE '%'||:query||'%' OR openCode LIKE '%'||:query||'%' OR shortName LIKE '%'||:query||'%'  ORDER BY openCode ASC")
    fun getSyllabusSearch(query: String): Flow<List<SyllabusModel>>

    @Query("SELECT * FROM syllabus_table WHERE subject LIKE '%'||:query||'%' OR code LIKE '%'||:query||'%' OR openCode LIKE '%'||:query||'%' OR shortName LIKE '%'||:query||'%'  ORDER BY openCode ASC")
    suspend fun getSyllabusSearchSync(query: String): List<SyllabusModel>

    @Query("SELECT * FROM syllabus_table WHERE openCode LIKE '%'||:course||'%' ORDER BY openCode ASC")
    fun getSyllabusAsCourse(course: String): Flow<List<SyllabusModel>>


    /***
     *Get Syllabus for Syllabus section.
     */
    @Query("SELECT * FROM syllabus_table WHERE openCode LIKE '%'||:query||'%' and type Like '%'||:type||'%' ORDER BY listOrder ASC")
    fun getSyllabusType(query: String, type: String): Flow<List<SyllabusModel>>

    @Query("SELECT * FROM syllabus_table WHERE openCode LIKE '%'||:query||'%' and type Like '%'||:type||'%' ORDER BY listOrder ASC")
    fun getSyllabusTypeLive(query: String, type: String): LiveData<List<SyllabusModel>>

    @Query("SELECT * FROM syllabus_table WHERE openCode LIKE '%'||:query||'%' and type Like '%'||:type||'%' ORDER BY listOrder ASC")
    suspend fun getSyllabusTypeList(query: String, type: String): List<SyllabusModel>

    /***
     *Get Syllabus for Home
     */
    @Query("SELECT * FROM syllabus_table WHERE openCode LIKE '%'||:query||'%' and type Like '%'||:type||'%' and isChecked=1 ORDER BY listOrder ASC")
    fun getSyllabusHome(query: String, type: String): Flow<List<SyllabusModel>>

    @Query("SELECT * FROM syllabus_table WHERE openCode LIKE '%'||:query||'%' and type Like '%'||:type||'%' and isChecked=1 ORDER BY listOrder ASC")
    suspend fun getSyllabusHomeList(query: String, type: String): List<SyllabusModel>

    /***
     *  Get Syllabus for edit
     */
    @Query("SELECT * FROM syllabus_table WHERE openCode LIKE '%'||:query||'%' ORDER BY listOrder ASC")
    fun getSyllabusEdit(query: String): Flow<List<SyllabusModel>>


    @Query("SELECT * FROM syllabus_table WHERE openCode = :query")
    suspend fun getSyllabusDeepLink(query: String): SyllabusModel?

    /**
     *Update syllabus
     */
    @Update
    suspend fun updateSyllabus(syllabus: SyllabusModel)

    /**
     * Reset Settings
     */
    @Query("UPDATE syllabus_table SET isChecked =1 WHERE openCode like '%'||:openCode||'%'")
    suspend fun reset(openCode: String)

    @Query("UPDATE syllabus_table SET isChecked =0 ")
    suspend fun resetAll()

    /**
     * Delete All Network Entities
     */
    @Delete
    suspend fun deleteNetwork(syllabus: SyllabusModel)

    /***
     * Inset all called for only ones
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(syllabusModel: SyllabusModel)


    /**
     * @since 4.0.3
     * @author Ayaan
     */
    @Query("UPDATE syllabus_table SET isAdded =0 WHERE isAdded = 1")
    suspend fun updateSyllabusAddedInAttendance()

    /**
     * @since 4.0.3
     * @author Ayaan
     */
    @Query("UPDATE syllabus_table SET isAdded =:isAdded WHERE subject = :attendance")
    suspend fun updateSyllabusAddedInAttendance(attendance: String, isAdded: Int)


    /**
     * Delete all the data from the table
     * @since 4.0.3
     * @author Ayaan
     */
    @Query("DELETE FROM syllabus_table")
    suspend fun deleteAll()

    /**
     * Insert all the syllabus at ones
     * @since 4.0.3
     * @author Ayaan
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(syllabus: List<SyllabusModel>)

    /**
     * Get syllabus by their name
     * @since 4.0.3
     * @author Ayaan
     */
    @Query("SELECT * FROM syllabus_table WHERE subject = :subject")
    suspend fun getSyllabus(subject: String): SyllabusModel?

    @Query("UPDATE syllabus_table SET isChecked = 1")
    suspend fun updateIsChecked()
}