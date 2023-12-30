package com.atech.core.module

import android.content.Context
import androidx.room.Room
import com.atech.core.datasource.room.BitDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Singleton
    @Provides
    fun getDatabase(
        @ApplicationContext context: Context,
        callback: BitDatabase.SyllabusCallback
    ): BitDatabase =
        Room.databaseBuilder(
            context,
            BitDatabase::class.java,
            BitDatabase.DATABASE_NAME
        ).addCallback(callback).fallbackToDestructiveMigrationFrom()
            .build()

    @Singleton
    @Provides
    fun getAttendanceDao(database: BitDatabase) = database.attendanceDao()

    @Singleton
    @Provides
    fun getSyllabusDao(database: BitDatabase) = database.syllabusDao()

    @Singleton
    @Provides
    fun getLibraryDao(database: BitDatabase) = database.libraryDao()

    @Singleton
    @Provides
    fun getChatDao(database: BitDatabase) = database.chatDao()
}