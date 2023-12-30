package com.atech.core.datasource.room.library

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {


    @Query("SELECT * FROM library_table order by markAsReturn asc,returnDate asc")
    fun getAll(): Flow<List<LibraryModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(library: LibraryModel): Long

    @Update
    suspend fun updateBook(library: LibraryModel)


    @Delete
    suspend fun deleteBook(library: LibraryModel)


    @Query("DELETE FROM library_table")
    suspend fun deleteAll()


}