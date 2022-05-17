/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/1/21, 1:09 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/1/21, 11:14 AM
 */



package com.aatec.bit.fragments.attendance.delete_all

import androidx.lifecycle.ViewModel
import com.aatec.core.data.room.attendance.AttendanceDao
import com.aatec.core.data.room.syllabus.SyllabusDao
import com.aatec.core.utils.BitAppScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAllViewModel @Inject constructor(
    private val attendanceDao: AttendanceDao,
    private val syllabusDao: SyllabusDao,
    @BitAppScope
    private val appScope: CoroutineScope
) : ViewModel() {
    fun deleteAll() = appScope.launch {
        syllabusDao.updateSyllabusAddedInAttendance()
        attendanceDao.deleteAll()
    }
}