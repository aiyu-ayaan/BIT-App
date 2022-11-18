package com.atech.bit.ui.fragments.course.sem_choose

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.atech.core.api.ApiRepository
import com.atech.core.data.room.syllabus.SyllabusDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class ChooseSemViewModel @Inject constructor(
    private val syllabusDao: SyllabusDao,
    private val state: SavedStateHandle,
    private val apiRepository: ApiRepository
) : ViewModel() {

    val request = state.get<String>("request")

    val sem = MutableStateFlow("")


    val theory = sem.flatMapLatest { sem ->
        syllabusDao.getSyllabusType(sem, "Theory")

    }.asLiveData()

    val lab = sem.flatMapLatest { sem ->
        syllabusDao.getSyllabusType(sem, "Lab")

    }.asLiveData()
    val pe = sem.flatMapLatest { sem ->
        syllabusDao.getSyllabusType(sem, "PE")

    }.asLiveData()

    var chooseSemNestedViewPosition: Int? = state["chooseSemNestedViewPosition"]
        set(value) {
            field = value
            state["chooseSemNestedViewPosition"] = value
        }

    var syllabusEnableModel: SemChooseFragment.SyllabusEnableModel =
        SemChooseFragment.SyllabusEnableModel()


    fun getOnlineSyllabus() = sem.flatMapLatest { semester ->
        apiRepository.getSyllabus(semester.lowercase())
    }.asLiveData()
}