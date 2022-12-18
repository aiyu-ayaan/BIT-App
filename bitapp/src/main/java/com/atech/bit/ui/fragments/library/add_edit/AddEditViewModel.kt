package com.atech.bit.ui.fragments.library.add_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.core.data.room.library.LibraryDao
import com.atech.core.data.room.library.LibraryModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val dao: LibraryDao
) : ViewModel() {
    val title = state["title"] ?: ""

    var libraryModel = state.get<LibraryModel>("library") ?: LibraryModel()
        set(value) {
            field = value
            state["library"] = value
        }


    fun addBook(libraryModel: LibraryModel) = viewModelScope.launch {
        dao.insertBook(libraryModel)
    }

    fun updateBook(libraryModel: LibraryModel) = viewModelScope.launch {
        dao.updateBook(libraryModel)
    }

    fun deleteBook(libraryModel: LibraryModel) = viewModelScope.launch {
        dao.deleteBook(libraryModel)
    }
}