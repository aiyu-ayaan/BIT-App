package com.atech.bit.ui.activities.main_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.atech.bit.ui.fragments.universal_dialog.UniversalDialogFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class DataShareViewModel @Inject constructor() : ViewModel() {

    private val _universalDialogState =
        MutableStateFlow<Pair<UniversalDialogFragment.UniversalDialogData?, Int>>(null to 0)

    val universalDialogData: LiveData<Pair<UniversalDialogFragment.UniversalDialogData?, Int>> =
        _universalDialogState.asLiveData()

    fun setUniversalDialogData(data: UniversalDialogFragment.UniversalDialogData?, id: Int) {
        _universalDialogState.value = data to id
    }
}