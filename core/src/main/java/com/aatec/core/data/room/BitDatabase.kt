/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/16/22, 12:09 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/15/22, 9:10 PM
 */



package com.aatec.core.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aatec.core.data.room.event.DateConverter
import com.aatec.core.data.room.event.EventCachedEntity
import com.aatec.core.data.room.event.EventDao
import com.aatec.core.data.room.holiday.HolidayCacheEntity
import com.aatec.core.data.room.holiday.HolidayDao
import com.aatec.core.data.room.notice.Notice3CacheEntity
import com.aatec.core.data.room.notice.Notice3Dao
import com.aatec.core.data.room.syllabus.SyllabusDao
import com.aatec.core.data.room.syllabus.SyllabusList
import com.aatec.core.data.room.syllabus.SyllabusModel
import com.aatec.core.data.room.timeTable.TimeTableCacheEntity
import com.aatec.core.data.room.timeTable.TimeTableDao
import com.aatec.core.data.room.attendance.*
import com.aatec.core.utils.BitAppScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(
    entities = [
        HolidayCacheEntity::class,
        AttendanceModel::class, EventCachedEntity::class,
        SyllabusModel::class, Notice3CacheEntity::class,
        TimeTableCacheEntity::class
    ],
    version = 5
)
@TypeConverters(
    DaysTypeConvector::class,
    StackTypeConvector::class,
    IsPresentTypeConvector::class,
    DateConverter::class
)
abstract class BitDatabase : RoomDatabase() {
    abstract fun holidayDao(): HolidayDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun eventDao(): EventDao
    abstract fun syllabusDap(): SyllabusDao
    abstract fun notice3Dao(): Notice3Dao
    abstract fun timeTableDao(): TimeTableDao


    companion object {
        const val DATABASE_NAME = "BIT App"

        /**
         * For Migration adding new column isHidden
         * @author Ayaan
         * @since 4.0.2
         */
        val migration_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE syllabus_table ADD COLUMN isChecked INTEGER DEFAULT 1")
                database.execSQL("ALTER TABLE syllabus_table ADD COLUMN fromNetwork INTEGER DEFAULT 0")
                database.execSQL("ALTER TABLE syllabus_table ADD COLUMN content TEXT DEFAULT ''")
                database.execSQL("ALTER TABLE syllabus_table ADD COLUMN book TEXT DEFAULT ''")
                database.execSQL("ALTER TABLE syllabus_table ADD COLUMN reference TEXT DEFAULT ''")
                database.execSQL("ALTER TABLE syllabus_table ADD COLUMN deprecated INTEGER DEFAULT 0")
                database.execSQL("DELETE FROM attendance_table")
            }
        }

        /**
         * For Migration adding new column isHidden
         * @author Ayaan
         * @since 4.0.3
         */
        val migration_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE syllabus_table ADD COLUMN isAdded INTEGER DEFAULT 0")
                database.execSQL("ALTER TABLE attendance_table ADD COLUMN fromSyllabus INTEGER DEFAULT 0")
                database.execSQL("ALTER TABLE event_table ADD COLUMN poster_link TEXT DEFAULT ''")
                database.execSQL("UPDATE syllabus_table SET fromNetwork = 0, content = NULL,book = NULL,reference = NULL WHERE openCode = 'bca512'")
            }
        }

        /**
         * For Migration adding new column isHidden
         * @author Ayaan
         * @since 4.0.4
         */
        val migration_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE attendance_table ADD COLUMN teacher TEXT DEFAULT ''")
                database.execSQL("ALTER TABLE attendance_table ADD COLUMN created INTEGER DEFAULT NULL")
            }
        }

        /**
         *
         * @author Ayaan
         * @since 4.0.5
         */
        val migration_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE event_table ADD COLUMN path TEXT DEFAULT ''")
                database.execSQL("CREATE TABLE `time_table_table`(`course` TEXT NOT NULL, `gender` TEXT NOT NULL, `sem` TEXT NOT NULL,`section` TEXT NOT NULL , `imageLink` TEXT NOT NULL ,`created` INTEGER NOT NULL , PRIMARY KEY(`created`))")
                database.execSQL("CREATE TABLE `notice_3_table`(`title` TEXT NOT NULL, `body` TEXT NOT NULL, `link` TEXT NOT NULL, `sender` TEXT NOT NULL, `path` TEXT NOT NULL, `created` INTEGER NOT NULL, PRIMARY KEY(`title`))")
                database.execSQL("DROP TABLE notice_table")
            }
        }


    }


    //Adding Syllabus
    class SyllabusCallback @Inject constructor(
        private val database: Provider<BitDatabase>,
        @BitAppScope private val appScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val dao = database.get().syllabusDap()
            appScope.launch {
                val syllabus = SyllabusList.syllabus
                for (s in syllabus) {
                    dao.insert(s)
                }
            }
        }
    }
}
