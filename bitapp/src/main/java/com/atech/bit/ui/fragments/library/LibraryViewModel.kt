package com.atech.bit.ui.fragments.library

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.atech.core.data.room.library.LibraryDao
import com.atech.core.room.library.LibraryModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val dao: LibraryDao
) : ViewModel() {

    val libraryList = dao.getAll().asLiveData()

    fun deleteBook(library: LibraryModel) = viewModelScope.launch {
        dao.deleteBook(library)
    }

    fun updateBook(library: LibraryModel) = viewModelScope.launch {
        dao.updateBook(library)
    }

}