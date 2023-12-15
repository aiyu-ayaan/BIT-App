package com.atech.course

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.atech.core.data_source.firebase.remote.model.CourseDetailModel
import com.atech.core.data_source.firebase.remote.model.CourseDetails
import com.atech.core.data_source.room.syllabus.SubjectType
import com.atech.core.use_case.KTorUseCase
import com.atech.core.use_case.SyllabusUIModel
import com.atech.core.use_case.SyllabusUseCase
import com.atech.core.utils.COURSE_DETAILS
import com.atech.core.utils.SYLLABUS_SOURCE_DATA
import com.atech.core.utils.SharePrefKeys
import com.atech.core.utils.fromJSON
import com.atech.course.utils.SyllabusEnableModel
import com.atech.course.utils.compareToCourseSem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val useCase: SyllabusUseCase,
    private val kTorUseCase: KTorUseCase,
    private val pref: SharedPreferences
) : ViewModel() {
    private val courseDetailsJson =
        (pref.getString(SharePrefKeys.CourseDetails.name, COURSE_DETAILS) ?: COURSE_DETAILS)

    val courseDetails = fromJSON(courseDetailsJson, CourseDetails::class.java)


    private val syllabusEnableModel: SyllabusEnableModel by lazy {
        pref.getString(
            SharePrefKeys.KeyToggleSyllabusSource.name, SYLLABUS_SOURCE_DATA
        )?.let {
            fromJSON(it, SyllabusEnableModel::class.java)!!
        } ?: fromJSON(
            SYLLABUS_SOURCE_DATA, SyllabusEnableModel::class.java
        )!!
    }

    private val _currentClickItem = mutableStateOf(CourseDetailModel("bca", 6))
    val currentClickItem: State<CourseDetailModel> get() = _currentClickItem


    private val _currentSem = mutableIntStateOf(0)
    val currentSem: State<Int> get() = _currentSem

    private var _onlineSyllabus =
        mutableStateOf<Triple<List<SyllabusUIModel>, List<SyllabusUIModel>, List<SyllabusUIModel>>>(
            Triple(
                emptyList(), emptyList(), emptyList()
            )
        )
    val onlineSyllabus: State<Triple<List<SyllabusUIModel>, List<SyllabusUIModel>, List<SyllabusUIModel>>> get() = _onlineSyllabus


    private val _isSelected = mutableStateOf(
        syllabusEnableModel.compareToCourseSem(
            _currentClickItem.value.name + _currentSem.intValue
        )
    )
    val isSelected: State<Boolean> get() = _isSelected

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

    private val _oneTimeEvent = MutableSharedFlow<OneTimeEvent>()
    val oneTimeEvent = _oneTimeEvent.asSharedFlow()

    fun onEvent(events: CourseEvents) {
        when (events) {
            is CourseEvents.NavigateToSemChoose -> {
                _currentClickItem.value = events.model
                _isSelected.value = syllabusEnableModel.compareToCourseSem(
                    _currentClickItem.value.name + _currentSem.intValue
                )
                _currentSem.intValue =
                    pref.getInt(SharePrefKeys.ChooseSemLastSelectedSem.name, 1).let {
                            if (_currentClickItem.value.sem < it) _currentClickItem.value.sem
                            else it
                        }
                if (_isSelected.value) getOnlineSubjects()
                else getAllSubjects()
            }

            is CourseEvents.OnSemChange -> {
                _currentSem.intValue = events.sem
                _isSelected.value = syllabusEnableModel.compareToCourseSem(
                    _currentClickItem.value.name + _currentSem.intValue
                )
                pref.edit().putInt(
                    SharePrefKeys.ChooseSemLastSelectedSem.name, events.sem
                ).apply()
                if (!isSelected.value) getAllSubjects()
                else getOnlineSubjects()
            }

            CourseEvents.OnSwitchToggle -> {
                _isSelected.value = !_isSelected.value
                if (!isSelected.value) getAllSubjects()
                else getOnlineSubjects()
            }

            is CourseEvents.ErrorDuringLoadingError -> {
                _isSelected.value = false
                viewModelScope.launch {
                    _oneTimeEvent.emit(OneTimeEvent.ShowSnackBar(events.message))
                }
            }
        }
    }

    private fun getOnlineSubjects() = viewModelScope.launch {
        try {
            _onlineSyllabus.value = Triple(
                emptyList(), emptyList(), emptyList()
            )
            _onlineSyllabus.value =
                kTorUseCase.fetchSyllabus("${_currentClickItem.value.name}${_currentSem.intValue}")
        } catch (e: Exception) {
            Log.d("AAA", "getOnlineSubjects: ${e.message}")
            onEvent(CourseEvents.ErrorDuringLoadingError("Can't load online syllabus. Check your internet connection."))
        }

    }

    private fun getAllSubjects() {
        theoryJob?.cancel()
        theoryJob = useCase.getSubjectsByType(
            courseSem = "${_currentClickItem.value.name}${_currentSem.intValue}".lowercase(),
            type = SubjectType.THEORY
        ).cachedIn(viewModelScope).onEach {
            _theory.value = it
        }.launchIn(viewModelScope)

        labJob?.cancel()
        labJob = useCase.getSubjectsByType(
            courseSem = "${_currentClickItem.value.name}${_currentSem.intValue}".lowercase(),
            type = SubjectType.LAB
        ).cachedIn(viewModelScope).onEach {
            _lab.value = it
        }.launchIn(viewModelScope)

        peJob?.cancel()
        peJob = useCase.getSubjectsByType(
            courseSem = "${_currentClickItem.value.name}${_currentSem.intValue}".lowercase(),
            type = SubjectType.PE
        ).cachedIn(viewModelScope).onEach {
            _pe.value = it
        }.launchIn(viewModelScope)
    }

    sealed class OneTimeEvent {
        data class ShowSnackBar(val message: String) : OneTimeEvent()
    }
}