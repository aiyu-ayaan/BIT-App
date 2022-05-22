package com.aatec.bit.ui.fragments.course.sem_choose

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.aatec.core.data.room.syllabus.SyllabusDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class ChooseSemViewModel @Inject constructor(
    private val syllabusDao: SyllabusDao,
    val state: SavedStateHandle
) : ViewModel() {

    val request = state.get<String>("request")

    val sem = MutableStateFlow("")


    val theory = sem.flatMapLatest {
        syllabusDao.getSyllabusType(it, "Theory")
    }.asLiveData()

    val lab = sem.flatMapLatest {
        syllabusDao.getSyllabusType(it, "Lab")
    }.asLiveData()
    val pe = sem.flatMapLatest {
        syllabusDao.getSyllabusType(it, "PE")
    }.asLiveData()

    var chooseSemNestedViewPosition: Int? = state.get("chooseSemNestedViewPosition")
        set(value) {
            field = value
            state.set("chooseSemNestedViewPosition", value)
        }
}