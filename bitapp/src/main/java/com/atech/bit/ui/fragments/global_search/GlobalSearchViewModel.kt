package com.atech.bit.ui.fragments.global_search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.bit.ui.fragments.global_search.model.SearchItem
import com.atech.core.api.ApiRepository
import com.atech.core.api.holiday.Holiday
import com.atech.core.data.room.syllabus.SyllabusDao
import com.atech.core.data.ui.events.EventRepository
import com.atech.core.data.ui.notice.NoticeRepository
import com.atech.core.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalSearchViewModel @Inject constructor(
    private val dao: SyllabusDao,
    private val apiRepository: ApiRepository,
    private val noticeRepository: NoticeRepository,
    private val eventRepository: EventRepository,
) : ViewModel() {

    val query = MutableStateFlow("none")
    private val _content: MutableLiveData<List<SearchItem>> = MutableLiveData()
    val searchContent: LiveData<List<SearchItem>> = _content

    val filterState = MutableStateFlow(FilterState())

    init {
        viewModelScope.launch {
            query.combine(filterState) { query, content ->
                Pair(query, content)
            }.collectLatest { (query, content) ->
                Log.d("AAA", ": $content")
                val list = mutableListOf<SearchItem>()
                if (content.syllabus)
                    dao.getSyllabusSearchSync(query).map { syllabus ->
                        SearchItem.Syllabus(syllabus)
                    }.let {
                        if (it.isNotEmpty()) {
                            list.add(SearchItem.Title("Syllabus"))
                            list.addAll(it)
                        }
                    }

                if (content.holiday) {
                    val holiday = holidayAsync(query)
                    holiday.await().let {
                        if (it.isNotEmpty()) {
                            list.add(SearchItem.Title("Holidays"))
                            list.addAll(it)
                        }
                    }
                }

                if (content.notice) {
                    val notice = noticeAsync(query)
                    notice.let {
                        if (it.await().isNotEmpty()) {
                            list.add(SearchItem.Title("Notice"))
                            list.addAll(it.await())
                        }
                    }
                }

                if (content.event) {
                    val event = eventAsync(query)
                    event.let {
                        if (it.await().isNotEmpty()) {
                            list.add(SearchItem.Title("Events"))
                            list.addAll(it.await())
                        }
                    }
                }
                _content.postValue(list)
            }
        }
    }

    private fun CoroutineScope.eventAsync(query: String) = async {
        eventRepository.getSearchEvent(query)
            .map { list ->
                list.map {
                    SearchItem.Event(it)
                }
            }.toList().flatten()
    }

    private fun CoroutineScope.noticeAsync(query: String) = async {
        noticeRepository.getNoticeSearch(query)
            .map { list ->
                list.map {
                    SearchItem.Notice(it)
                }
            }.toList().flatten()
    }

    private fun CoroutineScope.holidayAsync(query: String) = async {
        apiRepository.getHolidayData(query, filter = { q, h ->
            val filterList = mutableListOf<Holiday>()
            h.holidays.filter { it.day.contains(q, true) }.forEach { filterList.add(it) }
            h.holidays.filter { it.date.lowercase().contains(q) }.forEach { filterList.add(it) }
            h.holidays.filter { it.month.lowercase().contains(q) }.forEach { filterList.add(it) }
            h.holidays.filter { it.occasion.lowercase().contains(q) }.forEach { filterList.add(it) }
            filterList
        }).map { dataState ->
            when (dataState) {
                is DataState.Success -> {
                    dataState.data.holidays.map { holiday ->
                        SearchItem.Holiday(holiday)
                    }
                }

                else -> emptyList()
            }
        }.toList().flatten()
    }

    data class FilterState(
        val syllabus: Boolean = true,
        val holiday: Boolean = true,
        val notice: Boolean = true,
        val event: Boolean = true
    )


}