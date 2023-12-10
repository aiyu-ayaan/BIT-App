package com.atech.core.data_source.datastore

//import androidx.lifecycle.asLiveData
//import com.atech.core.room.attendance.Sort
//import com.atech.core.room.syllabus.SyllabusDao
//import javax.inject.Inject
//
//data class DataStoreCases @Inject constructor(
//    val getAll: GetAllPref,
//    val attendancePref: AttendancePref,
//    val updatePercentage: UpdatePercentage,
//    val updateCourse: UpdateCourse,
//    val updateSem: UpdateSem,
//    val updateCgpa: UpdateCgpa,
//    val updateAttendanceSort: UpdateAttendanceSort,
//    val reset: Reset,
//    val clearAll: ClearAll
//)
//
//class GetAllPref @Inject constructor(
//    private val preferencesManager: PreferencesManager
//) {
//    operator fun invoke() = preferencesManager.preferencesFlow.asLiveData()
//}
//
//class AttendancePref @Inject constructor(
//    private val preferencesManager: PreferencesManager
//) {
//    operator fun invoke() = preferencesManager.attendanceSortFLow.asLiveData()
//}
//
//class UpdatePercentage @Inject constructor(
//    private val preferencesManager: PreferencesManager
//) {
//    suspend operator fun invoke(defPercentage: Int) =
//        preferencesManager.updatePercentage(defPercentage)
//}
//
//class UpdateCourse @Inject constructor(
//    private val preferencesManager: PreferencesManager, private val syllabusDao: SyllabusDao
//) {
//    suspend operator fun invoke(value: String) {
//        preferencesManager.updateCourse(value)
//        syllabusDao.resetAll()
//    }
//}
//
//class UpdateSem @Inject constructor(
//    private val preferencesManager: PreferencesManager,
//) {
//    suspend operator fun invoke(value: String) {
//        preferencesManager.updateSem(value)
//    }
//}
//
//class UpdateCgpa @Inject constructor(
//    private val preferencesManager: PreferencesManager
//) {
//    suspend operator fun invoke(cgpa: Cgpa) = preferencesManager.updateCgpa(cgpa)
//}
//
//class UpdateAttendanceSort @Inject constructor(
//    private val preferencesManager: PreferencesManager
//) {
//    suspend operator fun invoke(sort: Sort) = preferencesManager.updateSort(sort)
//}
//
//class Reset @Inject constructor(
//    private val syllabusDao: SyllabusDao
//) {
//    suspend operator fun invoke(openCode: String) = syllabusDao.reset(openCode)
//}
//
//class ClearAll @Inject constructor(
//    private val preferencesManager: PreferencesManager
//) {
//    suspend operator fun invoke() = preferencesManager.clearAll()
//}