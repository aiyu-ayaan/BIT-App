package com.atech.core.api

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.atech.core.api.syllabus.SyllabusCacheDao
import com.atech.core.api.syllabus.model.*

@Database(
    entities = [
        Semesters::class
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(
    TheoryListTypeConverter::class,
    LabListTypeConverter::class,
    TheoryContentTypeConverter::class,
    LabContentTypeConverter::class,
    BooksNameTypeConverter::class,
)
abstract class ApiCacheDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "api_cache_database"
    }

    abstract fun syllabusCacheDao(): SyllabusCacheDao
}