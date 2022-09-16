package com.atech.bit.ui.fragments.course.sem_choose

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.atech.core.api.SyllabusRepository
import com.atech.core.data.room.syllabus.SyllabusDao
import com.atech.core.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class ChooseSemViewModel @Inject constructor(
    private val syllabusDao: SyllabusDao,
    val state: SavedStateHandle,
    val repository: SyllabusRepository
) : ViewModel() {

    val request = state.get<String>("request")

    val sem = MutableStateFlow("")


    val theory =
        sem.flatMapLatest { sem ->
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

    suspend fun getSyllabus() =
        repository.getSyllabus()

    suspend fun getOnlineSyllabus() = sem.flatMapLatest { semester ->
        flow {
            emit(DataState.Loading)
            kotlinx.coroutines.delay(100)
            if (semester.lowercase().replace("\\d+".toRegex(), "") == "bca") {
                val sem = repository.getSyllabus().semesters.filter {
                    it.sem == semester.replace(
                        "\\D+".toRegex(),
                        ""
                    ).toInt()
                }
                if (sem.isNotEmpty()) {
                    emit(DataState.Success(sem[0]))
                } else {
                    emit(DataState.Empty)
                }
            } else {
                emit(DataState.Empty)
            }
        }
    }

}