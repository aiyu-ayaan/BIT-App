package com.atech.core.use_case

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.atech.core.data_source.room.attendance.AttendanceDao
import com.atech.core.data_source.room.attendance.AttendanceModel
import com.atech.core.data_source.room.attendance.AttendanceSave
import com.atech.core.data_source.room.attendance.IsPresent
import com.atech.core.data_source.room.attendance.Sort
import com.atech.core.data_source.room.attendance.countTotalClass
import com.atech.core.utils.DEFAULTPAGESIZE
import com.atech.core.utils.INITIALlOADSIZE
import com.atech.core.utils.MAX_STACK_SIZE
import com.atech.core.utils.convertLongToTime
import kotlinx.coroutines.flow.Flow
import java.util.Deque
import javax.inject.Inject


data class AttendanceUseCase @Inject constructor(
    val getAllAttendance: GetAllAttendance,
    val updatePresentOrTotal: UpdatePresentOrTotal
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