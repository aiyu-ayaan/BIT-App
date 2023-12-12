package com.atech.attendance.screen.attendance.compose


import android.view.View
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.ColorUtils
import com.atech.attendance.R
import com.atech.attendance.util.getCurrentMonthList
import com.atech.attendance.util.getEventList
import com.atech.components.BottomPadding
import com.atech.core.data_source.room.attendance.AttendanceModel
import com.atech.core.data_source.room.attendance.IsPresent
import com.atech.theme.BITAppTheme
import com.atech.theme.SwipeGreen
import com.atech.theme.SwipeRed
import com.atech.theme.dividerOrCardColor
import com.atech.theme.grid_1
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun AttendanceCalenderView(
    modifier: Modifier = Modifier,
    model: AttendanceModel,
) {
    Column(
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(grid_1)
                .height(280.dp),
            colors = CardDefaults
                .cardColors(
                    containerColor = MaterialTheme.colorScheme.dividerOrCardColor
                )
        ) {
            var currentMonth by remember {
                mutableStateOf(
                    SimpleDateFormat(
                        "MMMM yyyy",
                        Locale.getDefault()
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
                    MaterialTheme.colorScheme.primary.toArgb(),
                    60
                )
                Text(
                    text = currentMonth,
                    style = MaterialTheme.typography.titleLarge
                )
                AndroidView(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    factory = {
                        View.inflate(
                            it,
                            R.layout.layout_calender_view,
                            null
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
                                setListener(object :
                                    CompactCalendarView.CompactCalendarViewListener {
                                    override fun onDayClick(dateClicked: Date?) {}
                                    override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                                        currentMonth = SimpleDateFormat(
                                            "MMMM yyyy",
                                            Locale.getDefault()
                                        ).format(firstDayOfNewMonth!!)
                                    }
                                })

                                model.getEventList().forEach(::addEvent)
                            }
                        }
                    }
                )
            }
        }
        Divider(
            color = MaterialTheme.colorScheme.onPrimary,
        )
        val items = model.getCurrentMonthList(data = Date())
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(grid_1),
            colors = CardDefaults
                .cardColors(
                    containerColor = MaterialTheme.colorScheme.dividerOrCardColor
                )
        ) {
            LazyColumn {
                items(items = items) { item ->
                    ShowDateItems(
                        model = item
                    )
                }
            }
        }
        BottomPadding()
    }
}

@Composable
fun ShowDateItems(
    modifier: Modifier = Modifier,
    model: IsPresent
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(end = grid_1),
            text = if (model.totalClasses == null) "1" else model.totalClasses.toString(),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge
        )
        Box(modifier = Modifier) {
            Divider(
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxHeight()  //fill the max height
                    .width(1.dp)
            )
            Box(
                modifier = Modifier
                    .size(grid_1)
                    .align(Alignment.Center)
                    .background(
                        color =
                        if (model.isPresent) SwipeGreen else SwipeRed,
                        shape = CircleShape
                    )
            )
        }
        Column(
            Modifier.padding(start = grid_1)
        ) {
            Text(
                text = SimpleDateFormat("dd-MMMM", Locale.getDefault())
                    .format(model.day),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = if (model.isPresent) "Present" else "Absent",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AttendanceCalenderViewPreview() {
    BITAppTheme {
        ShowDateItems(
            model = IsPresent(System.currentTimeMillis(), true)
        )
    }
}

