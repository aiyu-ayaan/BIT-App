package com.atech.bit.ui.screens.home.screen.notice

import com.atech.core.datasource.firebase.firestore.NoticeModel

sealed class NoticeScreenEvent {
    data class OnEventClick(
        val model: NoticeModel
    ) : NoticeScreenEvent()
}