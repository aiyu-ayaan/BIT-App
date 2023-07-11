package com.atech.bit.ui.fragments.home.adapter

import com.atech.bit.ui.fragments.home.HomeViewModelExr
import com.atech.bit.utils.HomeTopModel
import com.atech.core.firebase.firestore.NoticeModel
import com.atech.core.room.library.LibraryModel
import com.atech.course.sem.adapter.SyllabusUIModel
import com.atech.theme.CardHighlightModel

sealed class HomeItems {
    data class Highlight(
        val model: CardHighlightModel
    ) : HomeItems()

    data class Library(
        val data: List<LibraryModel>
    ) : HomeItems()

    data class Settings(
        val model: HomeTopModel
    ) : HomeItems()

    data class Title(val title: String) : HomeItems()

    data class Subject(val data: SyllabusUIModel) : HomeItems()

    data class Holiday(val data: com.atech.core.retrofit.client.Holiday) : HomeItems()

    data class Event(val data: List<HomeViewModelExr.EventHomeModel>) : HomeItems()

    data class Cgpa(val data: com.atech.core.datastore.Cgpa) : HomeItems()

    data class Attendance(
        val data: HomeViewModelExr.AttendanceHomeModel
    ) : HomeItems()

    data class Notice(
        val data: NoticeModel
    ) : HomeItems()

    object DevNote : HomeItems()
}