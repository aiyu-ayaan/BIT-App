package com.atech.core.use_case

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.atech.core.data_source.datastore.DataStoreCases
import com.atech.core.data_source.room.attendance.AttendanceDao
import com.atech.core.data_source.room.attendance.AttendanceModel
import com.atech.core.data_source.room.attendance.AttendanceSave
import com.atech.core.data_source.room.attendance.IsPresent
import com.atech.core.data_source.room.attendance.Sort
import com.atech.core.data_source.room.attendance.countTotalClass
import com.atech.core.data_source.room.attendance.toAttendanceModel
import com.atech.core.data_source.room.syllabus.SyllabusDao
import com.atech.core.utils.DEFAULTPAGESIZE
import com.atech.core.utils.INITIALlOADSIZE
import com.atech.core.utils.MAX_STACK_SIZE
import com.atech.core.utils.convertLongToTime
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.ArrayDeque
import java.util.Deque
import javax.inject.Inject


data class AttendanceUseCase @Inject constructor(
    val getAllAttendance: GetAllAttendance,
    val updatePresentOrTotal: UpdatePresentOrTotal,
    val getAttendanceById: GetAttendanceById,
    val addAttendance: AddAttendance,
    val updateAttendance: UpdateAttendance,
    val undoAttendance: UndoAttendance,
    val archiveAttendance: ArchiveAttendance,
    val deleteAttendance: DeleteAttendance,
    val deleteAllAttendance: DeleteAllAttendance,
    val getAllSubject: GetAllSubject,
    val addOrRemoveFromSyllabus: AddOrRemoveFromSyllabus,
    val getElementIdFromSubjectName: GetElementIdFromSubjectName
)

data class GetAllAttendance @Inject constructor(
    private val dao: AttendanceDao
) {
    operator fun invoke(
        sort: Sort = Sort()
    ): Flow<PagingData<AttendanceModel>> =
        Pager(
            config = PagingConfig(
                pageSize = DEFAULTPAGESIZE,
                enablePlaceholders = false,
                initialLoadSize = INITIALlOADSIZE
            )
        ) {
            dao.getAttendanceSorted(sort)
        }.flow

}

data class UpdatePresentOrTotal @Inject constructor(
    private val dao: AttendanceDao
) {
    @Suppress("UNCHECKED_CAST")
    suspend operator fun invoke(
        attendance: AttendanceModel,
        isPresent: Boolean = true
    ) {
        val stack: Deque<AttendanceSave> = attendance.stack.also {
            if (it.size > MAX_STACK_SIZE) {
                for (i in 0 until MAX_STACK_SIZE) {
                    it.removeLast()
                }
            }
        }
        val presentDays = attendance.days.presetDays.clone() as ArrayList<Long>
        val absentDays = attendance.days.absentDays.clone() as ArrayList<Long>
        val totalDays = attendance.days.totalDays.clone() as ArrayList<IsPresent>
        if (isPresent) {
            stack.push(
                AttendanceSave(
                    attendance.total, attendance.present, attendance.days.copy(
                        presetDays = attendance.days.presetDays,
                        totalDays = ArrayList(attendance.days.totalDays.map {
                            IsPresent(
                                it.day, it.isPresent, it.totalClasses
                            )
                        })
                    )
                )
            )
            presentDays.add(System.currentTimeMillis())
        } else {
            stack.push(
                AttendanceSave(
                    attendance.total,
                    attendance.present,
                    attendance.days.copy(
                        absentDays = attendance.days.absentDays,
                        totalDays = ArrayList(attendance.days.totalDays.map {
                            IsPresent(
                                it.day, it.isPresent, it.totalClasses
                            )
                        })
                    ),
                )
            )
            absentDays.add(System.currentTimeMillis())
        }

        when {
            totalDays.isEmpty() || totalDays.last().day.convertLongToTime("DD/MM/yyyy") != System.currentTimeMillis()
                .convertLongToTime("DD/MM/yyyy") ||
                    if (isPresent) !totalDays.last().isPresent else totalDays.last().isPresent ->//new Entry or new day
                totalDays.add(IsPresent(System.currentTimeMillis(), isPresent, totalClasses = 1))

            totalDays.isNotEmpty() && totalDays.last().totalClasses == null ->//old database migration
                totalDays.last().totalClasses = totalDays.countTotalClass(totalDays.size, isPresent)

            else ->//same day
                totalDays.last().totalClasses = totalDays.last().totalClasses?.plus(1)
        }
        dao.update(
            AttendanceModel(
                id = attendance.id,
                subject = attendance.subject,
                present = if (isPresent) attendance.present + 1 else attendance.present,
                total = attendance.total + 1,
                days = when (isPresent) {
                    true -> attendance.days.copy(presetDays = presentDays, totalDays = totalDays)
                    false -> attendance.days.copy(absentDays = absentDays, totalDays = totalDays)
                },
                stack = stack,
                fromSyllabus = attendance.fromSyllabus,
                teacher = attendance.teacher,
                isArchive = attendance.isArchive
            )
        )
    }
}

class GetAttendanceById @Inject constructor(
    private val dao: AttendanceDao
) {
    suspend operator fun invoke(id: Int) = dao.getAttendanceById(id)
}

class AddAttendance @Inject constructor(
    private val dao: AttendanceDao
) {
    suspend operator fun invoke(attendance: AttendanceModel) = dao.insert(attendance)
}

class UpdateAttendance @Inject constructor(
    private val dao: AttendanceDao
) {
    suspend operator fun invoke(old: AttendanceModel, updated: AttendanceModel) {
        when {
            old == updated -> { // no change
                return
            }

            (updated.subject != old.subject || updated.teacher != old.teacher)
                    && updated.present == old.present
                    && updated.total == old.total -> { // subject name or teacher name change

                dao.update(
                    old.copy(
                        subject = updated.subject,
                        teacher = updated.teacher
                    )
                )
            }

            else -> {
                val ques: Deque<AttendanceSave> = ArrayDeque()
                dao.update(
                    old.copy(
                        subject = updated.subject,
                        present = updated.present,
                        total = updated.total,
                        stack = ques,
                        teacher = updated.teacher
                    )
                )
            }
        }
    }
}

class UndoAttendance @Inject constructor(
    private val dao: AttendanceDao
) {
    suspend operator fun invoke(attendance: AttendanceModel) {
        val stack: Deque<AttendanceSave> = attendance.stack
        val save = stack.peekFirst()
        if (save != null) {
            stack.pop()
            val att = attendance.copy(
                present = save.present,
                total = save.total,
                days = save.days,
                stack = stack,
            )
            dao.update(att)
        }
    }
}


class ArchiveAttendance @Inject constructor(
    private val dao: AttendanceDao
) {
    suspend operator fun invoke(attendance: AttendanceModel, isArchive: Boolean = true) {
        dao.update(attendance.copy(isArchive = isArchive))
    }
}


class DeleteAttendance @Inject constructor(
    private val dao: AttendanceDao
) {
    suspend operator fun invoke(attendance: AttendanceModel) {
        dao.delete(attendance)
    }
}

class DeleteAllAttendance @Inject constructor(
    private val dao: AttendanceDao
) {
    suspend operator fun invoke() {
        dao.deleteAll()
    }
}

class GetAllSubject @Inject constructor(
    private val offlineMapper: OfflineSyllabusUIMapper,
    private val syllabusDao: SyllabusDao,
    private val attendanceDao: AttendanceDao,
    private val dataScoreCase: DataStoreCases,
    private val kTorUseCase: KTorUseCase
) {
    suspend operator fun invoke(): Pair<List<SyllabusUIModel>, List<SyllabusUIModel>> =
        coroutineScope {
            val courseSem = dataScoreCase.getAll().first().courseWithSem
            val attendanceItem = attendanceDao.getAllAttendance()
            val job = async {
                val item = syllabusDao.getSyllabusEdit(courseSem).map {
                    offlineMapper.mapFormEntity(it)
                }.map {
                    it
                        .copy(
                            isAdded = attendanceItem.find { model ->
                                model.subject == it.subject
                            } != null
                        )
                }
                item
            }
            val job2 = async {
                try {
                    val item = kTorUseCase.fetchSyllabus(courseSem.lowercase()).let { item ->
                        item.first + item.second + item.third
                    }.map { item ->
                        item.copy(
                            isFromOnline = attendanceItem.find { model ->
                                model.subject == item.subject
                            } != null
                        )
                    }
                    item
                } catch (e: Exception) {
                    listOf(
                        SyllabusUIModel(
                            subject = "",
                            code = "",
                            credits = 0,
                            openCode = "",
                            type = "",
                            group = "",
                            shortName = "",
                            listOrder = 1,
                            subjectContent = null,
                            isChecked = false,
                            isAdded = false,
                            fromNetwork = false,
                            deprecated = false
                        )
                    )
                }
            }
            val offlineSyllabus = job.await()
            val onlineSyllabus = job2.await()

            Pair(onlineSyllabus, offlineSyllabus)
        }
}

data class AddOrRemoveFromSyllabus @Inject constructor(
    private val dao: AttendanceDao
) {
    suspend operator fun invoke(model: SyllabusUIModel, isAdded: Boolean) {
        if (isAdded) {
            dao.insert(model.toAttendanceModel())
        } else {
            dao.deleteFromSubjectName(model.subject)
        }
    }
}

data class GetElementIdFromSubjectName @Inject constructor(
    private val dao: AttendanceDao
) {
    suspend operator fun invoke(subject: String) = dao.getElementIdFromSubject(subject)
}
