package com.atech.bit.ui.screens.home

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.atech.bit.utils.SyllabusEnableModel
import com.atech.bit.utils.compareToCourseSem
import com.atech.core.datasource.datastore.Cgpa
import com.atech.core.datasource.firebase.firestore.EventModel
import com.atech.core.datasource.retrofit.model.Holiday
import com.atech.core.datasource.retrofit.model.HolidayType
import com.atech.core.datasource.room.syllabus.SubjectType
import com.atech.core.usecase.AuthUseCases
import com.atech.core.usecase.DataStoreCases
import com.atech.core.usecase.FirebaseCase
import com.atech.core.usecase.KTorUseCase
import com.atech.core.usecase.SyllabusUIModel
import com.atech.core.usecase.SyllabusUseCase
import com.atech.core.utils.SYLLABUS_SOURCE_DATA
import com.atech.core.utils.SharePrefKeys
import com.atech.core.utils.TAGS
import com.atech.core.utils.UpdateDataType
import com.atech.core.utils.fromJSON
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val syllabusUSeCase: SyllabusUseCase,
    private val retrofitUseCase: KTorUseCase,
    private val dateStoreCase: DataStoreCases,
    val firebaseCase: FirebaseCase,
    private val pref: SharedPreferences,
    private val authUseCases: AuthUseCases,
    calendar: Calendar,
) : ViewModel() {

    private val currentMonth = calendar.getDisplayName(
        Calendar.MONTH, Calendar.LONG, Locale.ENGLISH
    ) ?: "January"
    private val syllabusEnableModel: SyllabusEnableModel by lazy {
        pref.getString(
            SharePrefKeys.KeyToggleSyllabusSource.name, SYLLABUS_SOURCE_DATA
        )?.let {
            fromJSON(it, SyllabusEnableModel::class.java)!!
        } ?: fromJSON(
            SYLLABUS_SOURCE_DATA, SyllabusEnableModel::class.java
        )!!
    }

    private val _isOnlineSyllabusEnable = mutableStateOf(false)
    val isOnlineSyllabusEnable: State<Boolean> get() = _isOnlineSyllabusEnable

    private var _dateStoreJob: Job? = null
    private val _course = mutableStateOf("BCA")
    val course: State<String> get() = _course


    private val _sem = mutableStateOf("1")
    val sem: State<String> get() = _sem

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

    private val _currentCgpa = mutableStateOf(Cgpa())
    val currentCgpa: State<Cgpa> get() = _currentCgpa


    private var _onlineSyllabus =
        mutableStateOf<Triple<List<SyllabusUIModel>, List<SyllabusUIModel>, List<SyllabusUIModel>>>(
            Triple(
                emptyList(), emptyList(), emptyList()
            )
        )
    val onlineSyllabus: State<Triple<List<SyllabusUIModel>, List<SyllabusUIModel>, List<SyllabusUIModel>>> get() = _onlineSyllabus


    private val _holidays = mutableStateOf<List<Holiday>>(emptyList())
    val holidays: State<List<Holiday>> get() = _holidays

    private val _fetchEvents = mutableStateOf<List<EventModel>>(emptyList())
    val events: State<List<EventModel>> get() = _fetchEvents

    private var job: Job? = null

    fun onEvent(event: HomeScreenEvents) {
        when (event) {
            is HomeScreenEvents.ToggleOnlineSyllabusClick -> {
                _isOnlineSyllabusEnable.value = event.isOnline
                if (event.isOnline) getOnlineSubjects()
                else getAllSubjects()
            }

            is HomeScreenEvents.OnCourseChange -> {
                _course.value = event.value.first
                _sem.value = event.value.second
                _isOnlineSyllabusEnable.value = syllabusEnableModel.compareToCourseSem(
                    course.value + sem.value
                )
                viewModelScope.launch {
                    dateStoreCase.updateCourse.invoke(event.value.first)
                    dateStoreCase.updateSem.invoke(event.value.second)
                    updateCourseSem(
                        _course.value,
                        _sem.value
                    )
                }
                if (isOnlineSyllabusEnable.value) getOnlineSubjects()
                else getAllSubjects()
            }
        }
    }


    //    ------------------------------------------ Fetching Data -------------------------------------

    init {
        getDataStore()
        _isOnlineSyllabusEnable.value = syllabusEnableModel.compareToCourseSem(
            course.value + sem.value
        )
        if (isOnlineSyllabusEnable.value) getOnlineSubjects()
        else getAllSubjects()
        getHolidays()
        getAllEvents()
    }

    private fun getDataStore() {
        _dateStoreJob?.cancel()
        _dateStoreJob = dateStoreCase.getAll.invoke().onEach {
            _course.value = it.course
            _sem.value = it.sem
            _currentCgpa.value = it.cgpa
        }.launchIn(viewModelScope)
    }

    private fun getAllSubjects() {
        theoryJob?.cancel()
        theoryJob = syllabusUSeCase.getSubjectsByType(
            courseSem = "${course.value}${sem.value}".lowercase(), type = SubjectType.THEORY
        ).cachedIn(viewModelScope).onEach {
            _theory.value = it
        }.launchIn(viewModelScope)

        labJob?.cancel()
        labJob = syllabusUSeCase.getSubjectsByType(
            courseSem = "${course.value}${sem.value}".lowercase(), type = SubjectType.LAB
        ).cachedIn(viewModelScope).onEach {
            _lab.value = it
        }.launchIn(viewModelScope)

        peJob?.cancel()
        peJob = syllabusUSeCase.getSubjectsByType(
            courseSem = "${course.value}${sem.value}".lowercase(), type = SubjectType.PE
        ).cachedIn(viewModelScope).onEach {
            _pe.value = it
        }.launchIn(viewModelScope)
    }

    private fun getOnlineSubjects() = viewModelScope.launch {
        try {
            _onlineSyllabus.value = Triple(
                emptyList(), emptyList(), emptyList()
            )
            _onlineSyllabus.value =
                retrofitUseCase.fetchSyllabus("${course.value}${sem.value}".lowercase())
        } catch (e: Exception) {
            Log.d("AAA", "getOnlineSubjects: ${e.message}")
            _isOnlineSyllabusEnable.value = false
//            onEvent(CourseEvents.ErrorDuringLoadingError("Can't load online syllabus. Check your internet connection."))
        }
    }

    private fun getHolidays(
    ) = viewModelScope.launch {
        try {
            _holidays.value = retrofitUseCase.fetchHolidays.invoke(
                HolidayType.ALL,
                month = currentMonth
            )
        } catch (e: Exception) {
            _holidays.value = emptyList()
        }
    }

    private fun getAllEvents() {
        job?.cancel()
        job = firebaseCase.getEvent().onEach {
            _fetchEvents.value = it
        }.launchIn(viewModelScope)
    }

    private fun updateCourseSem(
        course: String,
        sem: String
    ) = viewModelScope.launch {
        if (!authUseCases.hasLogIn.invoke()) return@launch
        try {
            authUseCases.uploadData.invoke(
                UpdateDataType.UpdateCourseSem(
                    course = course,
                    sem = sem
                )
            )
        } catch (e: Exception) {
            Log.e(TAGS.BIT_ERROR.name, "uploadCgpa: ${e.message ?: "Error"}")
        }
    }
}