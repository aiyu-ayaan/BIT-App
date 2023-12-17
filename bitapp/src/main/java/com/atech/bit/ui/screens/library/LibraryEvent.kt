package com.atech.bit.ui.screens.library

import com.atech.core.datasource.room.library.LibraryModel

sealed class LibraryEvent {
    data class NavigateToAddEditScreen(val model: LibraryModel? = null) : LibraryEvent()

    data class OnBookNameChange(val value: String) : LibraryEvent()

    data class OnBookIdChange(val value: String) : LibraryEvent()

    data class OnIssueDateChange(val value: Long) : LibraryEvent()

    data class OnReturnDateChange(val value: Long) : LibraryEvent()

    data class PickDateClick(val pickFor: PickFor,val date : Long) : LibraryEvent()

    data object ResetValue : LibraryEvent()
}

enum class PickFor {
    ISSUE_DATE, RETURN_DATE
}