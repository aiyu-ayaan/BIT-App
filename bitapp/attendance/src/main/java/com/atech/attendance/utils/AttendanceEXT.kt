package com.atech.attendance.utils

import com.atech.attendance.AttendanceViewModel
import com.atech.core.room.attendance.AttendanceModel
import com.atech.core.room.attendance.AttendanceSave
import com.atech.core.room.attendance.IsPresent
import com.atech.core.utils.MAX_STACK_SIZE
import com.atech.core.utils.convertLongToTime
import java.util.Deque

@Suppress("UNCHECKED_CAST")
fun onCheckClick(viewModel: AttendanceViewModel, attendance: AttendanceModel) {
    val stack: Deque<AttendanceSave> = attendance.stack.also {
        if (it.size > MAX_STACK_SIZE) {
            for (i in 0 until MAX_STACK_SIZE) {
                it.removeLast()
            }
        }
    }
    val presentDays = attendance.days.presetDays.clone() as ArrayList<Long>
    val totalDays = attendance.days.totalDays.clone() as ArrayList<IsPresent>

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
    /**
     * @since 4.0.3
     * @author Ayaan
     */
    when {
        totalDays.isEmpty() || totalDays.last().day.convertLongToTime("DD/MM/yyyy") != System.currentTimeMillis()
                .convertLongToTime("DD/MM/yyyy") || !totalDays.last().isPresent ->//new Entry or new day or new session
            totalDays.add(IsPresent(System.currentTimeMillis(), true, totalClasses = 1))

        totalDays.isNotEmpty() && totalDays.last().totalClasses == null ->//old database migration
            totalDays.last().totalClasses = totalDays.countTotalClass(totalDays.size, true)

        else ->//same day
            totalDays.last().totalClasses = totalDays.last().totalClasses?.plus(1)
    }
    viewModel.update(
            AttendanceModel(
                    id = attendance.id,
                    subject = attendance.subject,
                    present = attendance.present + 1,
                    total = attendance.total + 1,
                    days = attendance.days.copy(presetDays = presentDays, totalDays = totalDays),
                    stack = stack,
                    fromSyllabus = attendance.fromSyllabus,
                    teacher = attendance.teacher,
                    isArchive = attendance.isArchive
            )
    )
//        communicator.hasChange = true // FIXME:  Communicator

}

@Suppress("UNCHECKED_CAST")
fun onWrongClick(viewModel: AttendanceViewModel, attendance: AttendanceModel) {
    val stack: Deque<AttendanceSave> = attendance.stack.also {
        if (it.size > MAX_STACK_SIZE) {
            for (i in 0 until MAX_STACK_SIZE) {
                it.removeLast()
            }
        }
    }
    val absentDays = attendance.days.absentDays.clone() as ArrayList<Long>
    val totalDays = attendance.days.totalDays.clone() as ArrayList<IsPresent>
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
    /**
     * @since 4.0.3
     * @author Ayaan
     */
    when {
        totalDays.isEmpty() || totalDays.last().day.convertLongToTime("DD/MM/yyyy") != System.currentTimeMillis()
                .convertLongToTime("DD/MM/yyyy") || totalDays.last().isPresent ->//new Entry or new day
            totalDays.add(IsPresent(System.currentTimeMillis(), false, totalClasses = 1))

        totalDays.isNotEmpty() && totalDays.last().totalClasses == null ->//old database migration
            totalDays.last().totalClasses = totalDays.countTotalClass(totalDays.size, false)

        else ->//same day
            totalDays.last().totalClasses = totalDays.last().totalClasses?.plus(1)
    }
    viewModel.update(
            AttendanceModel(
                    id = attendance.id,
                    subject = attendance.subject,
                    present = attendance.present,
                    total = attendance.total + 1,
                    days = attendance.days.copy(
                            absentDays = absentDays, totalDays = totalDays
                    ),
                    stack = stack,
                    fromSyllabus = attendance.fromSyllabus,
                    teacher = attendance.teacher,
                    isArchive = attendance.isArchive
            )
    )
//        communicator.hasChange = true // FIXME:  Communicator

}