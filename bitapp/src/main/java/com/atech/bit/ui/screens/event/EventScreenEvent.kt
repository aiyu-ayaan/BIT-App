/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.event

import com.atech.core.datasource.firebase.firestore.EventModel

sealed interface EventScreenEvent {
    data class OnEventClick(
        val model: EventModel
    ) : EventScreenEvent
}