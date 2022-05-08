/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/3/22, 6:46 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/3/22, 6:46 PM
 */

package com.aatec.core.data.ui.timeTable

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class TimeTableModel(
    val course: String,
    val gender: String,
    val sem: String,
    val section: String,
    val imageLink: String,
    val created: Long
) : Parcelable

class TimeTableDiffUtils : DiffUtil.ItemCallback<TimeTableModel>() {
    override fun areItemsTheSame(oldItem: TimeTableModel, newItem: TimeTableModel): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: TimeTableModel, newItem: TimeTableModel): Boolean =
        oldItem.created == newItem.created

}