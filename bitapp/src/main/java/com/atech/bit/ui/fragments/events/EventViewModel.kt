package com.atech.bit.ui.fragments.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.atech.core.firebase.firestore.Db
import com.atech.core.firebase.firestore.EventModel
import com.atech.core.firebase.firestore.FirebaseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val cases: FirebaseCases
) : ViewModel() {
    val allEvents = cases.getData
        .invoke(
            EventModel::class.java,
            Db.Event
        ).asLiveData()

    fun getAttach(path: String) = cases.getAttach.invoke(
        Db.Event,
        path
    ).asLiveData()
}