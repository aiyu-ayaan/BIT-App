package com.atech.bit.ui.screens.home.compose

import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.atech.bit.R
import com.atech.bit.ui.theme.grid_1
import com.atech.core.datasource.datastore.Cgpa
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.DecimalFormat

@Composable
fun CgpaHomeElement(
    modifier: Modifier = Modifier,
    cgpa: Cgpa = Cgpa()
) {
    if (cgpa.isAllZero) {
        return
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(grid_1)
    ) {
        val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
        val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
        HomeTitle(title = "GPA Graph")
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            factory = {
                View.inflate(
                    it,
                    R.layout.layout_char_view,
                    null
                )
            },
            update = {
                it.findViewById<LineChart>(R.id.char_view).setView(
                    cgpa = cgpa,
                    primaryColor,
                    textColor
                )
            }
        )
    }

}


fun LineChart.setView(
    cgpa: Cgpa,
    primaryColor: Int,
    textColor: Int
) = this.apply {
    legend.isEnabled = false
    xAxis.setDrawGridLines(false)
    axisRight.isEnabled = false
    axisLeft.setDrawGridLines(false)

    // set x axis values to down
    xAxis.position = XAxis.XAxisPosition.BOTTOM

    val list: MutableList<Entry> = mutableListOf()
    addData(list, cgpa)
    val barDataSet = LineDataSet(list, "SGPA")
    barDataSet.apply {
        color = primaryColor
        valueTextSize = 10f
        valueTextColor =
            textColor
    }
    val barData = LineData(barDataSet)
    xAxis.setLabelCount(list.size, /*force: */true)
    legend.textColor =
        textColor
    xAxis.textColor =
        textColor
    axisLeft.textColor =
        textColor
    axisRight.textColor =
        textColor
    description.text = "Average CGPA :- ${DecimalFormat("#0.00").format(cgpa.cgpa)}"
    description.textColor =
        textColor
    setPinchZoom(false)
    setScaleEnabled(false)
    animateXY(500, 500)
    invalidate()
    this.data = barData
}

private fun addData(list: MutableList<Entry>, cgpa: Cgpa) {
    if (cgpa.sem1 != 0.0) list.add(Entry(1f, cgpa.sem1.toFloat()))
    if (cgpa.sem2 != 0.0) list.add(Entry(2f, cgpa.sem2.toFloat()))
    if (cgpa.sem3 != 0.0) list.add(Entry(3f, cgpa.sem3.toFloat()))
    if (cgpa.sem4 != 0.0) list.add(Entry(4f, cgpa.sem4.toFloat()))
    if (cgpa.sem5 != 0.0) list.add(Entry(5f, cgpa.sem5.toFloat()))
    if (cgpa.sem6 != 0.0) list.add(Entry(6f, cgpa.sem6.toFloat()))
}
