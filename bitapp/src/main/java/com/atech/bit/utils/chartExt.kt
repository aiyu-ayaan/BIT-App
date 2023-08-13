package com.atech.bit.utils

import android.content.Context
import android.graphics.Color
import com.atech.core.datastore.Cgpa
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.color.MaterialColors
import java.text.DecimalFormat

fun LineChart.setView(context: Context, cgpa: Cgpa) = this.apply {
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
        color = MaterialColors.getColor(
            context, androidx.appcompat.R.attr.colorPrimary, Color.WHITE
        )
        valueTextSize = 10f
        valueTextColor =
            MaterialColors.getColor(context, com.atech.theme.R.attr.textColor, Color.WHITE)
    }
    val barData = LineData(barDataSet)
    xAxis.setLabelCount(list.size, /*force: */true)
    legend.textColor =
        MaterialColors.getColor(context, com.atech.theme.R.attr.textColor, Color.WHITE)
    xAxis.textColor =
        MaterialColors.getColor(context, com.atech.theme.R.attr.textColor, Color.WHITE)
    axisLeft.textColor =
        MaterialColors.getColor(context, com.atech.theme.R.attr.textColor, Color.WHITE)
    axisRight.textColor =
        MaterialColors.getColor(context, com.atech.theme.R.attr.textColor, Color.WHITE)
    description.text = "Average CGPA :- ${DecimalFormat("#0.00").format(cgpa.cgpa)}"
    description.textColor =
        MaterialColors.getColor(context, com.atech.theme.R.attr.textColor, Color.WHITE)
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