package com.atech.bit.ui.screens.home.screen.notice

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.core.datasource.firebase.firestore.FirebaseCase
import com.atech.core.datasource.firebase.firestore.GetAttach
import com.atech.core.datasource.firebase.firestore.NoticeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val case: FirebaseCase,
    val getAttach: GetAttach
) : ViewModel() {
    private val _fetchNotice = mutableStateOf<List<NoticeModel>>(emptyList())
    val fetchNotice: State<List<NoticeModel>> get() = _fetchNotice

    private var job: Job? = null


    init {
        fetchNotice()
    }

    private fun fetchNotice() {
        job?.cancel()
        job = case.getNotice()
            .onEach {
                _fetchNotice.value = it
            }.launchIn(viewModelScope)
    }
}