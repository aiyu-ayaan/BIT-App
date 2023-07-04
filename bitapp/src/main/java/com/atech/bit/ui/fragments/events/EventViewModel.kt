package com.atech.bit.ui.fragments.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.atech.core.firebase.firestore.Db
import com.atech.core.firebase.firestore.EventCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val cases: EventCases
) : ViewModel() {
    val allEvents = cases.getEvent.invoke().asLiveData()

    fun getAttach(path: String) = cases.getAttach.invoke(
        Db.Event,
        path
    ).asLiveData()
}