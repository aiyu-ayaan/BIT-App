package com.aatec.bit.ui.fragments.event.event_description

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.aatec.core.data.ui.event.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EventDescriptionModel @Inject constructor(
    private val state: SavedStateHandle,
    private val repository: EventRepository
) : ViewModel() {
    var path = state.get<String>("path")
        set(value) {
            field = value
            state.set("path", value)
        }
    var title = state.get<String>("title")
        set(value) {
            field = value
            state.set("title", value)
        }


    fun getEvent(path: String) =
        repository.getEventFromPath(path)
}