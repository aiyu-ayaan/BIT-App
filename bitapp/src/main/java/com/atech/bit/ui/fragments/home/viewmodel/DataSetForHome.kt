package com.atech.bit.ui.fragments.home.viewmodel

import com.atech.core.datastore.Cgpa
import com.atech.core.firebase.firestore.FirebaseCases
import com.atech.core.retrofit.ApiCases
import com.atech.core.room.attendance.AttendanceModel
import com.atech.core.room.library.LibraryModel
import com.atech.core.room.syllabus.SyllabusDao
import com.atech.course.sem.adapter.OfflineSyllabusUIMapper
import com.atech.course.sem.adapter.OnlineSyllabusUIMapper
import java.util.Calendar

class DataSetForHome(
    val courseWithSem: String,
    val isOnline: Boolean,
    val isPermissionGranted: Boolean,
    val attendance: List<AttendanceModel>,
    val library: List<LibraryModel>,
    val syllabusDao: SyllabusDao,
    val offlineSyllabusUIMapper: OfflineSyllabusUIMapper,
    val onlineSyllabusUIMapper: OnlineSyllabusUIMapper,
    val api: ApiCases,
    val calendar: Calendar,
    val firebaseCases: FirebaseCases,
    val cgpa: Cgpa,
)