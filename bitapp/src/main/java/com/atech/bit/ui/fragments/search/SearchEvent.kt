package com.atech.bit.ui.fragments.search

import androidx.compose.ui.focus.FocusState

sealed class SearchEvent {
    data class EnteredTitle(val value: String) : SearchEvent()
    data class ChangeTitleFocus(val focusState: FocusState) : SearchEvent()

}
