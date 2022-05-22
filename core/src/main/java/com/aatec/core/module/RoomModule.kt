/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 3/22/22, 10:45 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 3/22/22, 9:55 AM
 */



package com.aatec.core.module

import android.content.Context
import androidx.room.Room
import com.aatec.core.data.room.BitDatabase
import com.aatec.core.data.room.attendance.AttendanceDao
import com.aatec.core.data.room.event.EventDao
import com.aatec.core.data.room.holiday.HolidayDao
import com.aatec.core.data.room.notice.Notice3Dao
import com.aatec.core.data.room.syllabus.SyllabusDao
import com.aatec.core.utils.BitAppScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.util.*
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
        ).fallbackToDestructiveMigration()
            .addMigrations(BitDatabase.migration_1_2)
            .addMigrations(BitDatabase.migration_2_3)
            .addMigrations(BitDatabase.migration_3_4)
            .addMigrations(BitDatabase.migration_4_5)
            .addCallback(callback)
            .build()


    @Singleton
    @Provides
    fun getHolidayDao(noticeDatabase: BitDatabase): HolidayDao =
        noticeDatabase.holidayDao()

    @Singleton
    @Provides
    fun getAttendanceDao(noticeDatabase: BitDatabase): AttendanceDao =
        noticeDatabase.attendanceDao()

    @Singleton
    @Provides
    fun getEventDao(noticeDatabase: BitDatabase): EventDao =
        noticeDatabase.eventDao()

    @Singleton
    @Provides
    fun getSyllabusDao(noticeDatabase: BitDatabase): SyllabusDao =
        noticeDatabase.syllabusDap()

    @Singleton
    @Provides
    fun getNotice3Dao(noticeDatabase: BitDatabase): Notice3Dao =
        noticeDatabase.notice3Dao()


    @Singleton
    @Provides
    fun getCalender(): Calendar = Calendar.getInstance()

    @BitAppScope
    @Singleton
    @Provides
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

}