/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.ui.screens.attendance.attendance_screen.component


import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.ColorUtils
import com.atech.bit.R
import com.atech.bit.ui.comman.BottomPadding
import com.atech.bit.ui.theme.BITAppTheme
import com.atech.bit.ui.theme.SwipeGreen
import com.atech.bit.ui.theme.SwipeRed
import com.atech.bit.ui.theme.dividerOrCardColor
import com.atech.bit.ui.theme.grid_1
import com.atech.bit.utils.getCurrentMonthList
import com.atech.bit.utils.getEventList
import com.atech.bit.utils.getRelativeDateForAttendance
import com.atech.core.datasource.room.attendance.AttendanceModel
import com.atech.core.datasource.room.attendance.IsPresent
import com.atech.core.utils.getDate
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.pushpal.jetlime.EventPointType
import com.pushpal.jetlime.ItemsList
import com.pushpal.jetlime.JetLimeColumn
import com.pushpal.jetlime.JetLimeEvent
import com.pushpal.jetlime.JetLimeEventDefaults
import com.pushpal.jetlime.JetLimeExtendedEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalComposeApi::class)
@Composable
fun AttendanceCalenderView(
    modifier: Modifier = Modifier,
    model: AttendanceModel,
) {
    Column(
        modifier = modifier
    ) {
        var date by rememberSaveable {
            mutableStateOf(Date())
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(grid_1)
                .height(280.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.dividerOrCardColor
            )
        ) {
            var currentMonth by remember {
                mutableStateOf(
                    SimpleDateFormat(
                        "MMMM yyyy", Locale.getDefault()
                    ).format(System.currentTimeMillis())
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val currentDayBackgroundColor = MaterialTheme.colorScheme.primary.toArgb()
                val currentDayTextColor = MaterialTheme.colorScheme.onPrimary.toArgb()
                val currentSelectedDayBackgroundColor = ColorUtils.setAlphaComponent(
                    MaterialTheme.colorScheme.primary.toArgb(), 60
                )
                Text(
                    text = currentMonth, style = MaterialTheme.typography.titleLarge
                )
                AndroidView(modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(), factory = {
                    View.inflate(
                        it, R.layout.layout_calender_view, null
                    ).also { view ->
                        view.findViewById<CompactCalendarView>(R.id.calendar_view).apply {
                            setCurrentDayBackgroundColor(
                                currentDayBackgroundColor
                            )
                            setCurrentDayTextColor(
                                currentDayTextColor
                            )
                            setCurrentSelectedDayBackgroundColor(
                                currentSelectedDayBackgroundColor
                            )
                            shouldDrawIndicatorsBelowSelectedDays(true)
                            setListener(object : CompactCalendarView.CompactCalendarViewListener {
                                override fun onDayClick(dateClicked: Date?) {}
                                override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                                    currentMonth = SimpleDateFormat(
                                        "MMMM yyyy", Locale.getDefault()
                                    ).format(firstDayOfNewMonth!!)
                                    date = firstDayOfNewMonth
                                }
                            })

                            model.getEventList().forEach(::addEvent)
                        }
                    }
                })
            }
        }
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(grid_1),
//            colors = CardDefaults
//                .cardColors(
//                    containerColor = MaterialTheme.colorScheme.dividerOrCardColor
//                )
//        ) {
//            LazyColumn {
//                items(items = model.getCurrentMonthList(data = date)) { item ->
//                    ShowDateItems(
//                        model = item
//                    )
//                }
//            }
        AnimatedVisibility(visible = model.getCurrentMonthList(data = date).isNotEmpty()) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onPrimary,
            )
            val items = model.getCurrentMonthList(data = date)
            JetLimeColumn(
                modifier = Modifier.padding(16.dp),
                itemsList = ItemsList(items),
            ) { index, item, position ->
                JetLimeExtendedEvent(style = JetLimeEventDefaults.eventStyle(
                    position = position,
                    pointAnimation = if (index == 0) JetLimeEventDefaults.pointAnimation() else null,
                    pointType = if (index == 0) EventPointType.filled(0.4f) else EventPointType.Default,
                    /*pointStrokeColor = if (item.isPresent) MaterialTheme.colorScheme.primary else SwipeRed,*/
                    pointFillColor = if (item.isPresent) SwipeGreen else SwipeRed,
                ), additionalContent = {
                    ExtendedEventContent(
                        item = item
                    )
                }) {
                    VerticalEventContent(
                        item = item
                    )
                }
            }
        }
        BottomPadding()
    }
}

@Composable
fun VerticalEventContent(item: IsPresent, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 12.dp)
                .padding(top = 8.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            text = if (item.isPresent) "Present" else "Absent",
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            fontSize = 14.sp,
            text = item.day.getDate(),
        )
    }
}


@Composable
fun ExtendedEventContent(item: IsPresent, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .wrapContentHeight(),
    ) {
        Column(
            modifier = Modifier
                .wrapContentWidth()
                .padding(grid_1),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                text = if (item.totalClasses == null || item.totalClasses == 1) "Class: 1" else "Classes: ${item.totalClasses.toString()}"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AttendanceCalenderViewPreview() {
    BITAppTheme {

    }
}

