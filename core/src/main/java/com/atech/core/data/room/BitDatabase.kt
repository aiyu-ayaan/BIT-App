/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/16/22, 12:09 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/15/22, 9:10 PM
 */



package com.atech.core.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.atech.core.data.room.attendance.*
import com.atech.core.data.room.events.DateConverter
import com.atech.core.data.room.events.EventsCacheEntity
import com.atech.core.data.room.events.EventsDao
import com.atech.core.data.room.holiday.HolidayCacheEntity
import com.atech.core.data.room.holiday.HolidayDao
import com.atech.core.data.room.notice.Notice3CacheEntity
import com.atech.core.data.room.notice.Notice3Dao
import com.atech.core.data.room.syllabus.SyllabusDao
import com.atech.core.data.room.syllabus.SyllabusList
import com.atech.core.data.room.syllabus.SyllabusModel
import com.atech.core.utils.BitAppScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(
    entities = [
        HolidayCacheEntity::class, AttendanceModel::class,
        SyllabusModel::class, Notice3CacheEntity::class,
        EventsCacheEntity::class
    ],
    version = 8
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
    abstract fun syllabusDap(): SyllabusDao
    abstract fun notice3Dao(): Notice3Dao
    abstract fun eventDao(): EventsDao


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
                database.execSQL("CREATE TABLE `notice_3_table`(`title` TEXT NOT NULL, `body` TEXT NOT NULL, `link` TEXT NOT NULL, `sender` TEXT NOT NULL, `path` TEXT NOT NULL, `created` INTEGER NOT NULL, PRIMARY KEY(`title`))")
                database.execSQL("DROP TABLE notice_table")
            }
        }

        val migration_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE event_table")
                database.execSQL("CREATE TABLE `events_table`(`created` INTEGER NOT NULL,`title` TEXT NOT NULL,`content` TEXT NOT NULL, `insta_link` TEXT NOT NULL, `logo_link` TEXT NOT NULL,`path` TEXT NOT NULL,`society` TEXT NOT NULL, `video_link` TEXT NOT NULL,  PRIMARY KEY(`title`))")
            }
        }

        val migration_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("UPDATE syllabus_table SET openCode ='bba63' where openCode ='bba53'")
                database.execSQL("UPDATE syllabus_table SET openCode ='bba64' where openCode ='bba54'")
                database.execSQL("UPDATE syllabus_table SET openCode ='bba611' where openCode ='bba55'")
                database.execSQL("UPDATE syllabus_table SET openCode ='bba610' where openCode ='bba56'")
                database.execSQL("UPDATE syllabus_table SET openCode ='bba619' where openCode ='bba57'")
                database.execSQL("UPDATE syllabus_table SET openCode ='bba620' where openCode ='bba58'")
                database.execSQL("UPDATE syllabus_table SET openCode ='bba621' where openCode ='bba59'")
            }
        }
        val migration_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("UPDATE syllabus_table SET openCode ='BBA53' where openCode ='BBA63'")
                database.execSQL("UPDATE syllabus_table SET openCode ='BBA54' where openCode ='BBA64'")
                database.execSQL("UPDATE syllabus_table SET openCode ='BBA55' where openCode ='BBA611'")
                database.execSQL("UPDATE syllabus_table SET openCode ='BBA56' where openCode ='BBA610'")
                database.execSQL("UPDATE syllabus_table SET openCode ='BBA57' where openCode ='BBA619'")
                database.execSQL("UPDATE syllabus_table SET openCode ='BBA58' where openCode ='BBA620'")
                database.execSQL("UPDATE syllabus_table SET openCode ='BBA59' where openCode ='BBA621'")
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
