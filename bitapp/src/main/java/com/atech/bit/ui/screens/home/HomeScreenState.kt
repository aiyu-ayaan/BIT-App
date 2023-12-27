package com.atech.bit.ui.screens.home

import androidx.paging.PagingData
import com.atech.core.datasource.datastore.Cgpa
import com.atech.core.datasource.firebase.firestore.EventModel
import com.atech.core.datasource.retrofit.model.Holiday
import com.atech.core.usecase.SyllabusUIModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


data class HomeScreenState(
    val course: String = "BCA",
    val sem: String = "1",
    val currentCgpa: Cgpa = Cgpa(),
    val isOnlineSyllabusEnable: Boolean = false,
    val onlineSyllabus: Triple<List<SyllabusUIModel>, List<SyllabusUIModel>, List<SyllabusUIModel>> = Triple(
        emptyList(), emptyList(), emptyList()
    ),
    val offTheory: StateFlow<PagingData<SyllabusUIModel>> = MutableStateFlow(PagingData.empty()),
    val offLab: StateFlow<PagingData<SyllabusUIModel>> = MutableStateFlow(PagingData.empty()),
    val offPractical: StateFlow<PagingData<SyllabusUIModel>> = MutableStateFlow(PagingData.empty()),
    val holiday: List<Holiday> = emptyList(),
    val events: StateFlow<List<EventModel>> = MutableStateFlow(emptyList()),
)


data class HomeScreechScreenState(
    val onlineSyllabus: Triple<List<SyllabusUIModel>, List<SyllabusUIModel>, List<SyllabusUIModel>> = Triple(
        emptyList(), emptyList(), emptyList()
    ),
    val offSyllabus: StateFlow<PagingData<SyllabusUIModel>> = MutableStateFlow(PagingData.empty()),
    val holiday: List<Holiday> = emptyList(),
    val events: StateFlow<List<EventModel>> = MutableStateFlow(emptyList()),
)