package com.atech.attendance.add_from_syllabus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.atech.core.datastore.DataStoreCases
import com.atech.core.datastore.FilterPreferences
import com.atech.core.retrofit.ApiCases
import com.atech.core.room.attendance.AttendanceDao
import com.atech.core.room.attendance.AttendanceModel
import com.atech.core.room.attendance.Days
import com.atech.core.room.syllabus.SyllabusDao
import com.atech.core.utils.getData
import com.atech.course.sem.adapter.OfflineSyllabusUIMapper
import com.atech.course.sem.adapter.OnlineSyllabusUIMapper
import com.atech.course.sem.adapter.SyllabusUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFromSyllabusViewModel @Inject constructor(
    dataStoreCases: DataStoreCases,
    private val apiCases: ApiCases,
    private val attendanceDao: AttendanceDao,
    private val syllabusDao: SyllabusDao,
    private val offlineSyllabusUIMapper: OfflineSyllabusUIMapper,
    private val onlineSyllabusUIMapper: OnlineSyllabusUIMapper
) : ViewModel() {

    val isOnline = MutableStateFlow(false)
    private val userPref = dataStoreCases.getAll.invoke().asFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSubjects(): Flow<List<SyllabusUIModel>> = isOnline.combine(userPref) { isOnline, user ->
        Pair(user, isOnline)
    }.flatMapLatest { (user, isOnline) ->
        val online = onlineSource(user)
        val offline = offlineSource(user)
        if (!isOnline)
            offline
        else
            online
    }

    private suspend fun onlineSource(user: FilterPreferences) =
        attendanceDao.getAllAttendance().combine(
            apiCases.syllabus.invoke(user.courseWithSem.lowercase())
        ) { _, data ->
            data
        }.map { dataState ->
            dataState.getData()?.let { res ->
                val syllabusList = (res.semester?.subjects?.lab
                    ?: emptyList()) + (res.semester?.subjects?.theory
                    ?: emptyList()) + (res.semester?.subjects?.pe ?: emptyList())

                map(
                    onlineSyllabusUIMapper.mapFromEntityList(syllabusList)
                )

            } ?: emptyList()
        }


    private fun offlineSource(user: FilterPreferences) =
        syllabusDao.getSyllabusEdit(user.courseWithSem).map {
            offlineSyllabusUIMapper.mapFromEntityList(it)
        }

    private suspend fun map(list: List<SyllabusUIModel>) =
        list.map { syllabusUIModel ->
            syllabusUIModel.copy(
                isAdded = attendanceDao.checkSubjectFromOnline(syllabusUIModel.subject) != null
            )
        }

    suspend fun findAttendance(syllabusUIModel: SyllabusUIModel) =
        attendanceDao.findSyllabus(syllabusUIModel.subject)


    fun addSubject(subject: String) = viewModelScope.launch {
        attendanceDao.insert(defaultAttendanceModel(subject))
        if (!isOnline.value) {
            updateSyllabus(subject, true)
        }
    }

    fun removeSubject(subject: String) = viewModelScope.launch {
        attendanceDao.findSyllabus(subject)?.let {
            attendanceDao.delete(it)
        }
        if (!isOnline.value)
            updateSyllabus(subject, false)
    }

    private fun updateSyllabus(subject: String, isAdded: Boolean) = viewModelScope.launch {
        syllabusDao.getSyllabus(subject)?.let {
            syllabusDao.updateSyllabus(
                it.copy(
                    isAdded = isAdded
                )
            )
        }
    }

    private fun defaultAttendanceModel(sub: String) = AttendanceModel(
        subject = sub,
        present = 0,
        total = 0,
        fromSyllabus = true,
        days = Days(
            presetDays = arrayListOf(),
            absentDays = arrayListOf(),
            totalDays = arrayListOf()
        ),
        teacher = "",
        fromOnlineSyllabus = isOnline.value
    )

}