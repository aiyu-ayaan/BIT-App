/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/16/22, 12:09 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/16/22, 11:56 AM
 */

package com.aatec.core.data.ui.notice

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import com.aatec.bit.data.Newtork.Notice.Attach
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Notice3(
    val title: String,
    val body: String,
    val attach: String,
    val link: String,
    val sender: String,
    val path: String,
    val created: Long
) : Parcelable

@Parcelize
@Keep
data class SendNotice3(
    val notice: Notice3,
    val attach: MutableList<Attach>?
) : Parcelable



class DiffUtilNotice3() : DiffUtil.ItemCallback<Notice3>() {
    override fun areItemsTheSame(oldItem: Notice3, newItem: Notice3): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: Notice3, newItem: Notice3): Boolean =
        oldItem.title == newItem.title

}