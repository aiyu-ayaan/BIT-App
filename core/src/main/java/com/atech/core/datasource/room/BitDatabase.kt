package com.atech.core.datasource.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.atech.core.datasource.room.attendance.AttendanceDao
import com.atech.core.datasource.room.attendance.AttendanceModel
import com.atech.core.datasource.room.attendance.DaysTypeConvector
import com.atech.core.datasource.room.attendance.IsPresentTypeConvector
import com.atech.core.datasource.room.attendance.StackTypeConvector
import com.atech.core.datasource.room.chat.ChatDao
import com.atech.core.datasource.room.chat.ChatModel
import com.atech.core.datasource.room.library.LibraryDao
import com.atech.core.datasource.room.library.LibraryModel
import com.atech.core.datasource.room.syllabus.SyllabusDao
import com.atech.core.datasource.room.syllabus.SyllabusList
import com.atech.core.datasource.room.syllabus.SyllabusModel
import com.atech.core.utils.BitAppScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider


@Database(
    entities = [
        AttendanceModel::class,
        SyllabusModel::class,
        LibraryModel::class,
        ChatModel::class
    ],
    version = 1,
)
@TypeConverters(
    DaysTypeConvector::class,
    StackTypeConvector::class,
    IsPresentTypeConvector::class,
)
abstract class BitDatabase : RoomDatabase() {
    abstract fun attendanceDao(): AttendanceDao
    abstract fun syllabusDao(): SyllabusDao
    abstract fun libraryDao(): LibraryDao

    abstract fun chatDao(): ChatDao

    companion object {
        const val DATABASE_NAME = "bit_database"
    }

    class SyllabusCallback @Inject constructor(
        private val database: Provider<BitDatabase>,
        @BitAppScope private val appScope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
//            val dao = database.get().attendanceDao()
            val syllabusDao = database.get().syllabusDao()
//            appScope.launch {
//                (1..100).toList().map { AttendanceModel("Subject $it") }
//                    .onEach {
//                        dao.insert(it)
//                    }
//            }
            appScope.launch {
                val syllabus = SyllabusList.syllabus
                syllabusDao.insertAll(syllabus)
                syllabusDao.updateIsChecked()
            }
        }
    }
}