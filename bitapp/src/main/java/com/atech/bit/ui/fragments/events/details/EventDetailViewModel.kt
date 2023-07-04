package com.atech.bit.ui.fragments.events.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.atech.core.firebase.firestore.Db
import com.atech.core.firebase.firestore.EventCases
import com.atech.core.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val case: EventCases
) : ViewModel() {
    private val path: String = state["path"] ?: ""

    var playWhenReady = state.get<Boolean>("playWhenReady") ?: false
        set(value) {
            field = value
            state["playWhenReady"] = value
        }
    var currentItem = state.get<Int>("currentItem") ?: 0
        set(value) {
            field = value
            state["currentItem"] = value
        }
    var playbackPosition = state.get<Long>("playbackPosition") ?: 0
        set(value) {
            field = value
            state["playbackPosition"] = value
        }

    fun getEvent() = try {
        case.getEventDetails.invoke(path).map {
            if (it == null) DataState.Empty
            else DataState.Success(it)
        }
    } catch (e: Exception) {
        MutableStateFlow(DataState.Error(e))
    }.asLiveData()

    fun getAttach() = try {
        case.getAttach.invoke(
            Db.Event, path
        )
    } catch (e: Exception) {
        emptyFlow()
    }.asLiveData()
}