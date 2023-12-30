package com.atech.bit.ui.screens.event

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.core.datasource.firebase.firestore.EventModel
import com.atech.core.usecase.FirebaseCase
import com.atech.core.usecase.GetAttach
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val firebaseCase: FirebaseCase,
    val getAttach: GetAttach,
    private val state: SavedStateHandle
) : ViewModel() {

    val eventId = state.get<String>("eventId") ?: ""

    private val _fetchEvents = mutableStateOf<List<EventModel>>(emptyList())
    val fetchEvents: State<List<EventModel>> get() = _fetchEvents

    private var job: Job? = null

    private val _currentClickEvent = mutableStateOf(EventModel())
    val currentClickEvent: State<EventModel?> get() = _currentClickEvent


    fun onEvent(event: EventScreenEvent) {
        when (event) {
            is EventScreenEvent.OnEventClick ->
                _currentClickEvent.value = event.model
        }
    }

    init {
        fetchEvents()
    }

    private fun fetchEvents() {
        job?.cancel()
        job = firebaseCase.getEvent().onEach {
            _fetchEvents.value = it
            if (eventId != "")
                _currentClickEvent.value = it.find { it1 -> it1.path == eventId } ?: EventModel(
                    title = "Something went wrong",
                    content = "Try some time later !! 🥲🥹"
                )

        }.launchIn(viewModelScope)
    }
}