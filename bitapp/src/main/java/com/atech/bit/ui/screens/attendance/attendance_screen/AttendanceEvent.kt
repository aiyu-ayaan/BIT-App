package com.atech.bit.ui.screens.attendance.attendance_screen

import com.atech.core.data_source.room.attendance.Sort
import com.atech.core.datasource.room.attendance.AttendanceModel
import com.atech.core.usecase.SyllabusUIModel

sealed class AttendanceEvent {
    data class ChangeAttendanceValue(
        val attendanceModel: AttendanceModel,
        val value: Int = 1,
        val isPresent: Boolean = true
    ) : AttendanceEvent()


    data class UndoAttendanceState(
        val attendanceModel: AttendanceModel
    ) : AttendanceEvent()

    data class DeleteAttendance(
        val attendanceModel: AttendanceModel
    ) : AttendanceEvent()

    data class ArchiveAttendance(
        val attendanceModel: AttendanceModel
    ) : AttendanceEvent()

    data object RestorerAttendance : AttendanceEvent()

    data class ItemSelectedClick(
        val attendanceModel: AttendanceModel,
        val isAdded: Boolean = true
    ) : AttendanceEvent()

    data class SelectAllClick(
        val attendanceModelList: List<AttendanceModel>,
        val isAdded: Boolean = true
    ) :
        AttendanceEvent()

    data object ClearSelection : AttendanceEvent()
    data object SelectedItemToArchive : AttendanceEvent()
    data object DeleteSelectedItems : AttendanceEvent()

    data class AddFromSyllabusItemClick(
        val model: SyllabusUIModel,
        val isAdded: Boolean
    ) : AttendanceEvent()


    data class ArchiveItemClick(
        val attendanceModel: AttendanceModel,
        val isAdded: Boolean = true
    ) : AttendanceEvent()

    data class ArchiveSelectAllClick(
        val attendanceModelList: List<AttendanceModel>,
        val isAdded: Boolean = true
    ) : AttendanceEvent()

    data object ArchiveScreenUnArchiveSelectedItems : AttendanceEvent()
    data object ArchiveScreenDeleteSelectedItems : AttendanceEvent()


    data class UpdateSettings(val percentage: Int, val sort: Sort) : AttendanceEvent()
}