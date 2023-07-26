package com.atech.bit.ui.fragments.universal_dialog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UniversalDialogViewModel @Inject constructor(
    state: SavedStateHandle
) : ViewModel() {
    val data = state.get<UniversalDialogFragment.UniversalDialogData>("universal_dialog_data")
}