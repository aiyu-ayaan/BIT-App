package com.atech.bit.ui.fragments.home

import com.atech.bit.ui.fragments.home.adapter.HomeItems
import com.atech.bit.utils.HomeTopModel
import com.atech.core.retrofit.ApiCases
import com.atech.core.room.syllabus.SyllabusDao
import com.atech.core.utils.getData
import com.atech.course.sem.adapter.OfflineSyllabusUIMapper
import com.atech.course.sem.adapter.OnlineSyllabusUIMapper
import com.atech.course.sem.adapter.SyllabusUIModel
import com.atech.theme.CardHighlightModel
import com.atech.theme.R
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

object HomeViewModelExr {

    fun topView(list: MutableList<HomeItems>) {
        list.add(
            HomeItems.Highlight(
                CardHighlightModel(
                    "Notification is disabled",
                    "Allow Notification to get latest notice and announcement",
                    R.drawable.ic_notice
                )
            )
        )
        list.add(
            HomeItems.Settings(
                HomeTopModel(
                    title = "Your Subjects",
                )
            )
        )
    }

    suspend fun offlineDataSource(
        syllabusDao: SyllabusDao,
        offlineSyllabusUIMapper: OfflineSyllabusUIMapper,
        courseSem: String
    ) =
        Triple(
            offlineSyllabusUIMapper.mapFromEntityList(
                syllabusDao.getSyllabusTypeList(
                    courseSem,
                    "Theory"
                )
            ),
            offlineSyllabusUIMapper.mapFromEntityList(
                syllabusDao.getSyllabusTypeList(
                    courseSem,
                    "Lab"
                )
            ),
            offlineSyllabusUIMapper.mapFromEntityList(
                syllabusDao.getSyllabusTypeList(
                    courseSem,
                    "PE"
                )
            )
        ).mapToHomeItems()

    suspend fun onlineDataSource(
        api: ApiCases,
        onlineSyllabusUIMapper: OnlineSyllabusUIMapper,
        courseSem: String
    ) =
        api.syllabus.invoke(courseSem.lowercase()).map { dataStore ->
            (dataStore.getData()?.let { syllabus ->
                Triple(
                    onlineSyllabusUIMapper.mapFromEntityList(
                        (syllabus.semester?.subjects?.theory) ?: emptyList()
                    ),
                    onlineSyllabusUIMapper.mapFromEntityList(
                        (syllabus.semester?.subjects?.lab) ?: emptyList()
                    ),
                    onlineSyllabusUIMapper.mapFromEntityList(
                        (syllabus.semester?.subjects?.pe) ?: emptyList()
                    )
                )
            } ?: emptyTriple()).mapToHomeItems()
        }.toList().flatten()


    val devNote = HomeItems.DevNote
    private fun Triple<List<SyllabusUIModel>, List<SyllabusUIModel>, List<SyllabusUIModel>>.mapToHomeItems(): List<HomeItems> {
        val list = mutableListOf<HomeItems>()
        if (first.isNotEmpty()) {
            list.add(HomeItems.Title("Theory"))
            list.addAll(first.map { HomeItems.Subject(it) })
        }
        if (second.isNotEmpty()) {
            list.add(HomeItems.Title("Lab"))
            list.addAll(second.map { HomeItems.Subject(it) })
        }
        if (third.isNotEmpty()) {
            list.add(HomeItems.Title("PE"))
            list.addAll(third.map { HomeItems.Subject(it) })
        }
        return list
    }

    private fun emptyTriple(): Triple<List<SyllabusUIModel>, List<SyllabusUIModel>, List<SyllabusUIModel>> =
        Triple(emptyList(), emptyList(), emptyList())
}