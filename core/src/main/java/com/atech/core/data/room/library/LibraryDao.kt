package com.atech.core.data.room.library

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {


    @Query("SELECT * FROM library_table order by returnDate desc")
    fun getAll(): Flow<List<LibraryModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLibrary(library: LibraryModel)

    @Update
    suspend fun updateLibrary(library: LibraryModel)


    @Delete
    suspend fun deleteLibrary(library: LibraryModel)

    @Query("DELETE FROM library_table")
    suspend fun deleteAllLibrary()

}