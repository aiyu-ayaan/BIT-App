package com.atech.course

import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.atech.core.firebase.remote.model.CourseDetailModel
import com.atech.core.firebase.remote.model.CourseDetails
import com.atech.core.room.syllabus.SubjectType
import com.atech.core.use_case.SyllabusUIModel
import com.atech.core.use_case.SyllabusUseCase
import com.atech.core.utils.COURSE_DETAILS
import com.atech.core.utils.SharePrefKeys
import com.atech.core.utils.fromJSON
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val useCase: SyllabusUseCase,
    pref: SharedPreferences
) : ViewModel() {
    private val courseDetailsJson =
        (pref.getString(SharePrefKeys.CourseDetails.name, COURSE_DETAILS) ?: COURSE_DETAILS)

    val courseDetails = fromJSON(courseDetailsJson, CourseDetails::class.java)

    private val _currentClickItem = mutableStateOf(CourseDetailModel("bca", 6))
    val currentClickItem: State<CourseDetailModel> get() = _currentClickItem

    private val _currentSem = mutableIntStateOf(1)
    val currentSem: State<Int> get() = _currentSem


    private val _theory: MutableStateFlow<PagingData<SyllabusUIModel>> =
        MutableStateFlow(PagingData.empty())
    val theory get() = _theory.asStateFlow()
    private var theoryJob: Job? = null

    private val _lab = MutableStateFlow(PagingData.empty<SyllabusUIModel>())
    val lab get() = _lab.asStateFlow()
    private var labJob: Job? = null

    private val _pe = MutableStateFlow(PagingData.empty<SyllabusUIModel>())
    val pe get() = _pe.asStateFlow()
    private var peJob: Job? = null

    fun onEvent(events: CourseEvents) {
        when (events) {
            is CourseEvents.NavigateToSemChoose -> {
                _currentClickItem.value = events.model
                getAllSubjects()
            }

            is CourseEvents.OnSemChange -> {
                _currentSem.intValue = events.sem
                getAllSubjects()
            }
        }
    }

    private fun getAllSubjects() {
        theoryJob?.cancel()
        theoryJob = useCase.getSubjectsByType(
            courseSem = "${_currentClickItem.value.name}${_currentSem.intValue}".lowercase(),
            type = SubjectType.THEORY
        ).cachedIn(viewModelScope)
            .onEach {
                _theory.value = it
            }
            .launchIn(viewModelScope)

        labJob?.cancel()
        labJob = useCase.getSubjectsByType(
            courseSem = "${_currentClickItem.value.name}${_currentSem.intValue}".lowercase(),
            type = SubjectType.LAB
        ).cachedIn(viewModelScope)
            .onEach {
                _lab.value = it
            }
            .launchIn(viewModelScope)

        peJob?.cancel()
        peJob = useCase.getSubjectsByType(
            courseSem = "${_currentClickItem.value.name}${_currentSem.intValue}".lowercase(),
            type = SubjectType.PE
        ).cachedIn(viewModelScope)
            .onEach {
                _pe.value = it
            }
            .launchIn(viewModelScope)

    }

}