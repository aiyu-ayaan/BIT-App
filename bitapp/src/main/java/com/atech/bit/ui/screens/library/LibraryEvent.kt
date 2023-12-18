package com.atech.bit.ui.screens.library

import com.atech.core.datasource.room.library.LibraryModel

sealed class LibraryEvent {

    data class OnTickClick(val model: LibraryModel) : LibraryEvent()

    data class OnDeleteClick(val model: LibraryModel) : LibraryEvent()

    data class NavigateToAddEditScreen(val model: LibraryModel? = null) : LibraryEvent()

    data class OnBookNameChange(val value: String) : LibraryEvent()

    data class OnBookIdChange(val value: String) : LibraryEvent()

    data class OnIssueDateChange(val value: Long) : LibraryEvent()

    data class OnReturnDateChange(val value: Long) : LibraryEvent()

    data class OnAlertDateChange(val value: Long) : LibraryEvent()

    data class PickDateClick(val pickFor: PickFor, val date: Long) : LibraryEvent()

    data object ResetValue : LibraryEvent()

    data class HasError(val hasError: Boolean = false, val message: String = "") : LibraryEvent()

    data class HasErrorInReminder(val hasError: Boolean = false, val message: String = " ") :
        LibraryEvent()

    data class SaveBook(
        val fromAlert : Boolean = false,
        val action: (() -> Unit)? = null
    ) : LibraryEvent()

    data class OnEventAdded(val eventId: Long) : LibraryEvent()

    data object OnEventDelete : LibraryEvent()

    data object UndoDelete : LibraryEvent()

    data object DeleteAll : LibraryEvent()
}

enum class PickFor {
    ISSUE_DATE, RETURN_DATE, REMINDER_DATE
}