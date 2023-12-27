package com.atech.bit.ui.screens.home.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AllInclusive
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.HolidayVillage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.atech.bit.ui.comman.EventItem
import com.atech.bit.ui.comman.singleElement
import com.atech.bit.ui.screens.course.components.SubjectItem
import com.atech.bit.ui.screens.course.components.SubjectTitle
import com.atech.bit.ui.screens.home.HomeScreechScreenState
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.grid_0_5
import com.atech.bit.ui.theme.grid_1
import com.atech.core.datasource.firebase.firestore.EventModel
import com.atech.core.usecase.SyllabusUIModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    state: HomeScreechScreenState = HomeScreechScreenState(),
    onSyllabusClick: (SyllabusUIModel) -> Unit = {},
    onEventClick: (EventModel) -> Unit = {}
) {
    var currentSelected by remember { mutableStateOf(FilterType.ALL) }
//    val onlineData = state.onlineSyllabus
    val syllabus = state.offSyllabus.collectAsLazyPagingItems()
    val holiday = state.holiday
    val events = state.events.value
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        singleElement(key = "radioButtons") {
            Header(currentSelected = currentSelected,
                onFilterSelected = { currentSelected = it }
            )
        }
//        if (currentSelected == FilterType.OnlineSYLLABUS ||
//            currentSelected == FilterType.ALL
//        )
//            onlineDataSource(
//                onlineData.first,
//                onlineData.second,
//                onlineData.third,
//                onClick = { model ->
//                    onSyllabusClick.invoke(model)
//                })
        if ((currentSelected == FilterType.OfflineSYLLABUS ||
                    currentSelected == FilterType.ALL) &&
            syllabus.itemCount != 0
        ) {
            singleElement(key = "syllabus") {
                SubjectTitle("Searched Syllabus")
            }
            items(count = syllabus.itemCount,
                key = syllabus.itemKey { model -> model.openCode + "offline" },
                contentType = syllabus.itemContentType { "Theory" }) { index ->
                syllabus[index]?.let { model ->
                    SubjectItem(
                        data = model, modifier = Modifier.animateItemPlacement(
                            animationSpec = spring(
                                dampingRatio = 2f, stiffness = 600f
                            )
                        ), onClick = {
                            onSyllabusClick.invoke(model)
                        }
                    )
                }
            }
        }
        if (currentSelected == FilterType.Holiday ||
            currentSelected == FilterType.ALL &&
            state.holiday.isNotEmpty()
        ) showHoliday(holiday)

        if (currentSelected == FilterType.Event ||
            currentSelected == FilterType.ALL &&
            state.events.value.isNotEmpty()
        ) {
            items(
                items = events,
                key = { event -> event.title + event.created }
            ) { model ->
                EventItem(
                    model = model,
                    onEventClick = {
                        onEventClick.invoke(model)
                    }
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Header(
    currentSelected: FilterType, onFilterSelected: (FilterType) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(grid_1)
            .animateContentSize()
    ) {
        FilterType.entries.forEach {
            val isSelected = it == currentSelected
            FilterChip(selected = isSelected, onClick = { onFilterSelected(it) }, label = {
                Text(text = it.value)
            }, modifier = Modifier.padding(end = grid_0_5), leadingIcon = {
                AnimatedVisibility(visible = isSelected) {
                    when (it) {
                        FilterType.ALL -> Icon(
                            imageVector = Icons.Outlined.AllInclusive, contentDescription = null
                        )
                        /*    FilterType.OnlineSYLLABUS -> Icon(
                                imageVector = Icons.Outlined.BookOnline, contentDescription = null
                            )*/
                        FilterType.OfflineSYLLABUS -> Icon(
                            imageVector = Icons.Outlined.Book, contentDescription = null
                        )

                        FilterType.Holiday -> Icon(
                            imageVector = Icons.Outlined.HolidayVillage,
                            contentDescription = null
                        )

                        FilterType.Event -> Icon(
                            imageVector = Icons.Outlined.Event, contentDescription = null
                        )
                    }
                }
            })
        }
    }
}

enum class FilterType(val value: String) {
    ALL("All"),

    /*OnlineSYLLABUS("Online Syllabus"),*/
    OfflineSYLLABUS("Syllabus"),
    Holiday("Holiday"),
    Event(
        "Event"
    ),
}


@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    BITAppTheme {
        SearchScreen()
    }
}