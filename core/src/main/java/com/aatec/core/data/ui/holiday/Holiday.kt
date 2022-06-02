/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 2/21/22, 10:27 AM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 2/21/22, 10:22 AM
 */



package com.aatec.core.data.ui.holiday

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize
import java.io.Serializable


@Keep
@Parcelize
data class Holiday(
    val sno: Int,
    val day: String,
    val date: String,
    val occasion: String,
    val month: String,
    val type: String
) : Parcelable, Serializable

class DiffCallbackHoliday : DiffUtil.ItemCallback<Holiday>() {
    override fun areItemsTheSame(oldItem: Holiday, newItem: Holiday): Boolean =
        oldItem.occasion == oldItem.occasion

    override fun areContentsTheSame(oldItem: Holiday, newItem: Holiday): Boolean =
        oldItem == newItem
}
