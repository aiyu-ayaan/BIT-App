package com.atech.bit.ui.screens.notice

import com.atech.core.datasource.firebase.firestore.NoticeModel

sealed interface NoticeScreenEvent {
    data class OnEventClick(
        val model: NoticeModel
    ) : NoticeScreenEvent
}