package com.atech.bit.ui.screens.event

import com.atech.core.datasource.firebase.firestore.EventModel

sealed interface EventScreenEvent {
    data class OnEventClick(
        val model: EventModel
    ) : EventScreenEvent
}