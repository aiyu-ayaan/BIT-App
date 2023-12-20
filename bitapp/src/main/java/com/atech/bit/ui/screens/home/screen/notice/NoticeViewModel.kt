package com.atech.bit.ui.screens.home.screen.notice

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.core.usecase.FirebaseCase
import com.atech.core.usecase.GetAttach
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

    private val _currentClickEvent = mutableStateOf(null as NoticeModel?)
    val currentClickEvent: State<NoticeModel?> get() = _currentClickEvent


    fun onEvent(event: NoticeScreenEvent) {
        when (event) {
            is NoticeScreenEvent.OnEventClick ->
                _currentClickEvent.value = event.model
        }
    }

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