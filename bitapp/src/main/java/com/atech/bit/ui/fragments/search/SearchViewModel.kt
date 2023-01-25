package com.atech.bit.ui.fragments.search

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.bit.ui.fragments.search.state.SearchTextFieldState
import com.atech.core.data.room.syllabus.SyllabusDao
import com.atech.core.data.room.syllabus.SyllabusModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val dao: SyllabusDao
) : ViewModel() {

    private var getNoteJob: Job? = null

    private val _searchTitle = mutableStateOf(
        SearchTextFieldState(
            hint = "Search For Any thing...",
        )
    )
    val searchTitle: State<SearchTextFieldState> = _searchTitle


    private val _searchSyllabus: MutableState<List<SyllabusModel>> = mutableStateOf(
        emptyList()
    )
    val searchSyllabus: State<List<SyllabusModel>> = _searchSyllabus

    fun setEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.EnteredTitle -> {
                _searchTitle.value = _searchTitle.value.copy(
                    text = event.value,
                    isHintVisible = false
                )
                searchSyllabus(_searchTitle.value.text)
            }

            is SearchEvent.ChangeTitleFocus -> {
                _searchTitle.value = _searchTitle.value.copy(
                    isHintVisible = !event.focusState.isFocused && _searchTitle.value.text.isBlank()
                )
            }
        }
    }

    private fun searchSyllabus(state: String) {
        getNoteJob?.cancel()
        getNoteJob = dao.getSyllabusSearch(state)
            .onEach { notes ->
                _searchSyllabus.value = notes
            }.launchIn(viewModelScope)
    }
}