package com.atech.bit.ui.fragments.home.util

import com.atech.bit.ui.fragments.home.FilterPreferences
import com.atech.bit.ui.fragments.home.adapter.HomeItems
import com.atech.bit.utils.HomeTopModel
import com.atech.core.retrofit.ApiCases
import com.atech.core.room.library.LibraryModel
import com.atech.core.room.syllabus.SyllabusDao
import com.atech.course.sem.adapter.OfflineSyllabusUIMapper
import com.atech.course.sem.adapter.OnlineSyllabusUIMapper
import com.atech.course.sem.adapter.SyllabusUIModel
import com.atech.theme.CardHighlightModel
import com.atech.theme.R
import com.atech.theme.compareDifferenceInDays
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.Date

class GetHomeData(
    private val filterPreferences: FilterPreferences,
) {

    fun getHomeItems() = channelFlow<List<HomeItems>> {
        val list = mutableListOf<HomeItems>()
        notificationAccess(list)
        getLibraryData(filterPreferences.library).also { list.addAll(it) }
        list.add(getTopSetting(filterPreferences.isOnline))
        getSyllabus(
            filterPreferences.isOnline,
            filterPreferences.courseWithSem,
            filterPreferences.syllabusDao,
            filterPreferences.api,
            filterPreferences.offlineSyllabusUIMapper,
            filterPreferences.onlineSyllabusUIMapper
        ).also { list.addAll(it) }



        list.add(HomeItems.DevNote)
        send(list)
        awaitClose()
    }.flowOn(Dispatchers.IO)

    private fun notificationAccess(list: MutableList<HomeItems>) {
        if (!filterPreferences.isPermissionGranted)
            list.add(
                HomeItems.Highlight(
                    CardHighlightModel(
                        "Notification is disabled",
                        "Allow Notification to get latest notice and announcement",
                        R.drawable.ic_notice
                    )
                )
            )
    }

    private suspend fun getLibraryData(
        library: List<LibraryModel>
    ) = coroutineScope {
        withContext(Dispatchers.IO) {
            val d = async {
                val list = mutableListOf<HomeItems>()
                library.filter3Day().also {
                    if (it.isNotEmpty()) {
                        list.add(HomeItems.Title("Library"))
                        list.add(HomeItems.Library(it))
                    }
                }
                list
            }
            d.await()
        }
    }

    private fun getTopSetting(
        isOnline: Boolean
    ) =
        HomeItems.Settings(
            HomeTopModel(
                title = "Your Subjects",
                isOnline = isOnline
            )
        )

    private suspend fun getSyllabus(
        isOnline: Boolean,
        courseSem: String,
        syllabusDao: SyllabusDao,
        apiCases: ApiCases,
        offlineSyllabusUIMapper: OfflineSyllabusUIMapper,
        onlineSyllabusUIMapper: OnlineSyllabusUIMapper
    ) =
        if (!isOnline)
            getOfflineData(courseSem, syllabusDao, offlineSyllabusUIMapper)
        else getOnlineSyllabusData(courseSem, apiCases, onlineSyllabusUIMapper)


    private suspend fun getOnlineSyllabusData(
        courseSem: String,
        apiCases: ApiCases,
        onlineSyllabusUIMapper: OnlineSyllabusUIMapper
    ): List<HomeItems> = coroutineScope {
        withContext(Dispatchers.IO) {
            val list = mutableListOf<HomeItems>()
            val res = async {
                try {
                    apiCases.fetchSyllabusAPI.invoke(courseSem.lowercase()).let { syllabus ->
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
                        ).mapToHomeItems()
                    }
                } catch (e: Exception) {
                    listOf(HomeItems.NoData)
                }
            }
            list.addAll(res.await())
            list
        }
    }

    private suspend fun getOfflineData(
        courseSem: String,
        syllabusDao: SyllabusDao,
        offlineSyllabusUIMapper: OfflineSyllabusUIMapper
    ) = coroutineScope {
        withContext(Dispatchers.IO) {
            val theory = async {
                offlineSyllabusUIMapper.mapFromEntityList(
                    syllabusDao.getSyllabusHomeList(
                        courseSem,
                        "Theory"
                    )
                )
            }
            val lab = async {
                offlineSyllabusUIMapper.mapFromEntityList(
                    syllabusDao.getSyllabusHomeList(
                        courseSem,
                        "Lab"
                    )
                )
            }
            val pe = async {
                offlineSyllabusUIMapper.mapFromEntityList(
                    syllabusDao.getSyllabusHomeList(
                        courseSem,
                        "PE"
                    )
                )
            }
            Triple(theory.await(), lab.await(), pe.await()).mapToHomeItems()
        }
    }


    //    Utils
    private fun List<LibraryModel>.filter3Day() = filter { book ->
        val diff = Date(book.returnDate).compareDifferenceInDays(
            Date(System.currentTimeMillis())
        )
        diff in 0..3 && !book.markAsReturn
    }

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

