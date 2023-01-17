package com.atech.bit.ui.fragments.search

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.atech.bit.ui.fragments.search.state.SearchTextFieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val state: SavedStateHandle
) : ViewModel() {

    private val _searchTitle = mutableStateOf(
        SearchTextFieldState(
            hint = "Search For Any thing...",
        )
    )
    val searchTitle = _searchTitle

    fun setEvent(event: SearchEvent) {
        when(event) {
            is SearchEvent.EnteredTitle -> {
                _searchTitle.value = _searchTitle.value.copy(
                    text = event.value,
                    isHintVisible = false
                )
            }
            is SearchEvent.ChangeTitleFocus -> {
                _searchTitle.value = _searchTitle.value.copy(
                    isHintVisible = !event.focusState.isFocused && _searchTitle.value.text.isBlank()
                )
            }
        }
    }
}