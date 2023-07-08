package com.atech.bit.ui.fragments.home.adapter

import com.atech.bit.ui.fragments.home.HomeViewModelExr
import com.atech.bit.utils.HomeTopModel
import com.atech.course.sem.adapter.SyllabusUIModel
import com.atech.theme.CardHighlightModel

sealed class HomeItems {
    data class Highlight(
        val model: CardHighlightModel
    ) : HomeItems()

    data class Settings(
        val model: HomeTopModel
    ) : HomeItems()

    data class Title(val title: String) : HomeItems()

    data class Subject(val data: SyllabusUIModel) : HomeItems()

    data class Holiday(val data: com.atech.core.retrofit.client.Holiday) : HomeItems()

    data class Event(val data: List<HomeViewModelExr.EventHomeModel>) : HomeItems()

    data class Cgpa(val model: com.atech.core.datastore.Cgpa) : HomeItems()

    object DevNote : HomeItems()
}