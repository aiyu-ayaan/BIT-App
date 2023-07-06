package com.atech.bit.ui.fragments.home.adapter

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

    object DevNote : HomeItems()
}