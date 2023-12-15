package com.atech.attendance.screen.attendance

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.atech.core.data_source.datastore.DataStoreCases
import com.atech.core.data_source.room.attendance.AttendanceModel
import com.atech.core.data_source.room.attendance.Sort
import com.atech.core.use_case.AttendanceUseCase
import com.atech.core.use_case.SyllabusUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val case: AttendanceUseCase,
    private val prefCase: DataStoreCases
) : ViewModel() {
    private val _attendance = MutableStateFlow<PagingData<AttendanceModel>>(PagingData.empty())
    val attendance: StateFlow<PagingData<AttendanceModel>> get() = _attendance.asStateFlow()

    private val _archiveAttendance = mutableStateOf<List<AttendanceModel>>(emptyList())
    val archiveAttendance: State<List<AttendanceModel>> get() = _archiveAttendance

    private var attendanceGetJob: Job? = null
    private var attendanceArchiveGetJob: Job? = null
    private var recentlyDeletedAttendance: AttendanceModel? = null

    private val _oneTimeAttendanceScreenEvent = MutableSharedFlow<OneTimeAttendanceEvent>()

    val oneTimeAttendanceScreenEvent = _oneTimeAttendanceScreenEvent.asSharedFlow()

    private val _selectedAttendance = mutableStateOf<List<AttendanceModel>>(emptyList())
    val selectedAttendance: State<List<AttendanceModel>> get() = _selectedAttendance


    private val _selectedArchiveItems = mutableStateOf<List<AttendanceModel>>(emptyList())
    val selectedArchiveItems: State<List<AttendanceModel>> get() = _selectedArchiveItems


    private val _fetchSyllabus = mutableStateOf<Pair<List<SyllabusUIModel>, List<SyllabusUIModel>>>(
        Pair(emptyList(), emptyList())
    )
    val fetchSyllabus: State<Pair<List<SyllabusUIModel>, List<SyllabusUIModel>>> get() = _fetchSyllabus

    private val _defaultPercentage = mutableIntStateOf(75)
    val defaultPercentage: State<Int> get() = _defaultPercentage

    private val _sort = mutableStateOf(Sort())
    val sort: State<Sort> get() = _sort

    init {
        getAllPref()
        getAttendance()
    }

    fun onEvent(event: AttendanceEvent) {
        when (event) {
            is AttendanceEvent.ChangeAttendanceValue -> {
                viewModelScope.launch {
                    case.updatePresentOrTotal(
                        attendance = event.attendanceModel,
                        isPresent = event.isPresent
                    )
                    getAttendance()
                }
            }

            is AttendanceEvent.UndoAttendanceState -> {
                viewModelScope.launch {
                    case.undoAttendance.invoke(event.attendanceModel)
                    getAttendance()
                }
            }

            is AttendanceEvent.ArchiveAttendance -> {
                viewModelScope.launch {
                    case.archiveAttendance(event.attendanceModel)
                    getAttendance()
                }
            }

            is AttendanceEvent.DeleteAttendance -> {
                viewModelScope.launch {
                    recentlyDeletedAttendance = event.attendanceModel
                    case.deleteAttendance(event.attendanceModel)
                    _oneTimeAttendanceScreenEvent.emit(
                        OneTimeAttendanceEvent.ShowUndoDeleteAttendanceMessage(
                            "Attendance Deleted"
                        )
                    )
                    getAttendance()
                }
            }

            AttendanceEvent.RestorerAttendance -> {
                viewModelScope.launch {
                    case.addAttendance(recentlyDeletedAttendance ?: return@launch)
                    recentlyDeletedAttendance = null
                    getAttendance()
                }
            }

            is AttendanceEvent.ItemSelectedClick -> {
                if (event.isAdded) {
                    _selectedAttendance.value += event.attendanceModel
                } else {
                    _selectedAttendance.value -= event.attendanceModel
                }
            }

            is AttendanceEvent.SelectAllClick -> {
                if (event.isAdded) {
                    _selectedAttendance.value = event.attendanceModelList
                } else {
                    _selectedAttendance.value = emptyList()
                }
            }

            AttendanceEvent.ClearSelection -> {
                _selectedAttendance.value = emptyList()
            }

            AttendanceEvent.SelectedItemToArchive -> {
                viewModelScope.launch {
                    _selectedAttendance.value.forEach {
                        case.archiveAttendance(it)
                    }
                    _selectedAttendance.value = emptyList()
                }
                getAttendance()
            }

            AttendanceEvent.DeleteSelectedItems -> {
                viewModelScope.launch {
                    _selectedAttendance.value.forEach {
                        case.deleteAttendance(it)
                    }
                    _selectedAttendance.value = emptyList()
                }
                getAttendance()
            }

            is AttendanceEvent.AddFromSyllabusItemClick -> {
                viewModelScope.launch {
                    case.addOrRemoveFromSyllabus(
                        model = event.model,
                        isAdded = event.isAdded
                    )
                }
                getAttendance()
            }

            is AttendanceEvent.ArchiveItemClick -> {
                if (event.isAdded) {
                    _selectedArchiveItems.value += event.attendanceModel
                } else {
                    _selectedArchiveItems.value -= event.attendanceModel
                }
            }

            AttendanceEvent.ArchiveScreenDeleteSelectedItems -> {
                viewModelScope.launch {
                    _selectedArchiveItems.value.forEach {
                        case.deleteAttendance(it)
                    }
                    _selectedArchiveItems.value = emptyList()
                }
                getAttendance()
                getAllArchiveAttendance()
            }

            AttendanceEvent.ArchiveScreenUnArchiveSelectedItems -> {
                viewModelScope.launch {
                    _selectedArchiveItems.value.forEach {
                        case.archiveAttendance(it, false)
                    }
                    _selectedArchiveItems.value = emptyList()
                }
                getAttendance()
                getAllArchiveAttendance()
            }

            is AttendanceEvent.ArchiveSelectAllClick -> {
                if (event.isAdded) {
                    _selectedArchiveItems.value = event.attendanceModelList
                } else {
                    _selectedArchiveItems.value = emptyList()
                }
            }

            is AttendanceEvent.UpdateSettings -> viewModelScope.launch {
                prefCase.updatePercentage.invoke(event.percentage)
                prefCase.updateAttendanceSort.invoke(event.sort)
                getAllPref()
                getAttendance()
            }
        }
    }

    fun getSubjectFromSyllabus() {
        viewModelScope.launch {
            _fetchSyllabus.value = case.getAllSubject()
        }
    }

    fun getAllArchiveAttendance() {
        attendanceArchiveGetJob?.cancel()
        attendanceArchiveGetJob = case.getAllArchiveSubject()
            .onEach {
                _archiveAttendance.value = it
            }.launchIn(viewModelScope)
    }

    suspend fun getElementIdFromSubject(sub: String) = case.getElementIdFromSubjectName(sub)

    sealed class OneTimeAttendanceEvent {
        data class ShowUndoDeleteAttendanceMessage(val message: String) : OneTimeAttendanceEvent()
    }

    private fun getAttendance() {
        attendanceGetJob?.cancel()
        attendanceGetJob = case.getAllAttendance(
            _sort.value
        )
            .cachedIn(viewModelScope)
            .onEach {
                _attendance.value = it
            }.launchIn(viewModelScope)
    }

//     ----------------------------------------------- Pref ----------------------------------------


    private var prefJob: Job? = null

    private fun getAllPref() {
        prefJob?.cancel()
        prefJob = prefCase.getAll.invoke().onEach {
            _defaultPercentage.intValue = it.defPercentage
            _sort.value = it.sort
        }.launchIn(viewModelScope)
    }
}