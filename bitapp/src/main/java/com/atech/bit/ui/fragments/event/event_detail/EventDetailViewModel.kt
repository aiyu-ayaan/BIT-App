package com.atech.bit.ui.fragments.event.event_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.atech.core.data.ui.events.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val repository: EventRepository
) : ViewModel() {
    val path = state.get<String>("path") ?: ""

    fun getEvent(path: String) = repository.getEventFromPath(path)

    fun getAttach(path: String) = repository.getAttachFromPath(path)
}