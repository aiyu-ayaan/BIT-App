package com.atech.bit.ui.fragments.library

import androidx.lifecycle.ViewModel
import com.atech.core.data.room.library.LibraryDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val dao: LibraryDao
) : ViewModel() {

    val libraryList = dao.getAll()

}