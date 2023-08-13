package com.atech.bit.ui.fragments.library.add_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.core.data.room.library.LibraryDao
import com.atech.core.room.library.LibraryModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val dao: LibraryDao
) : ViewModel() {

    var libraryModel = state.get<LibraryModel>("library") ?: LibraryModel()
        set(value) {
            field = value
            state["library"] = value
        }
    val title = state["title"] ?: ""

    fun updateBook(library: LibraryModel) = viewModelScope.launch {
        dao.updateBook(library)
    }

    fun addBook(libraryModel: LibraryModel) = viewModelScope.launch {
        dao.insertBook(libraryModel)
    }

}