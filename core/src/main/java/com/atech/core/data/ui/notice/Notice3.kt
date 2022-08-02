/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/16/22, 12:09 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/16/22, 11:56 AM
 */

package com.atech.core.data.ui.notice

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import com.atech.core.data.network.notice.Attach
import com.atech.core.data.ui.events.Events
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Notice3(
    val title: String,
    val body: String,
    val link: String,
    val sender: String,
    val path: String,
    val created: Long
) : Parcelable

@Parcelize
@Keep
data class SendNotice3(
    val notice: Notice3? = null,
    val event: Events? = null,
    val attach: List<Attach>
) : Parcelable


class DiffUtilNotice3() : DiffUtil.ItemCallback<Notice3>() {
    override fun areItemsTheSame(oldItem: Notice3, newItem: Notice3): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: Notice3, newItem: Notice3): Boolean =
        oldItem.title == newItem.title

}