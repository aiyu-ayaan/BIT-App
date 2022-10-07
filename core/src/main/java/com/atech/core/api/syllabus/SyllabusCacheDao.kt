package com.atech.core.api.syllabus

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.atech.core.api.syllabus.model.Semesters
import kotlinx.coroutines.flow.Flow

@Dao
interface SyllabusCacheDao {


    @Query("SELECT * FROM syllabus_cached_table WHERE id = :semester")
    fun getSyllabus(semester: String): Flow<Semesters?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyllabus(syllabusResponse: Semesters)

    @Query("DELETE FROM syllabus_cached_table")
    suspend fun deleteAll()
}