/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/1/21, 1:09 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/1/21, 11:14 AM
 */



package com.aatec.bit.utils

import com.aatec.core.data.room.attendance.AttendanceModel
import com.aatec.core.data.room.syllabus.SyllabusModel

/**
 * Main State Event
 *
 *@author Ayaan
 *@since 4.0.2
 */
sealed class MainStateEvent {
    object GetData : MainStateEvent()
    object NoInternet : MainStateEvent()
}


sealed class AddEvents {
    object Success : AddEvents()
    data class Error(val e: Exception) : AddEvents()
}

/**
 *Event when attendance is deleted
 *@see com.aatec.bit.ui.Fragments.Attendance.FragmentAttendance
 *@author Ayaan
 *@since 4.0.3
 */
sealed class AttendanceEvent {
    /**
     * Note :- syllabus is not use in FragmentEditSyllabus
     * @param attendance AttendanceModel
     * @param syllabus SyllabusModel
     * @see com.aatec.bit.ui.Fragments.EditSyllabus.FragmentEditSyllabus
     * @since 4.0.3
     */
    data class ShowUndoDeleteMessage(
        val attendance: AttendanceModel,
        val syllabus: SyllabusModel? = null
    ) : AttendanceEvent()
}