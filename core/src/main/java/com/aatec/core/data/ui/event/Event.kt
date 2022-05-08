/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 3/22/22, 10:45 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 3/22/22, 9:55 AM
 */



package com.aatec.core.data.ui.event

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Event(
    val created: Long,
    val date: String,
    val event_body: String,
    val event_title: String,
    val ins_link: String,
    val logo_link: String,
    val society: String,
    val web_link: String,
    val poster_link: String,
    val path: String
) : Parcelable


class EventDiffUtil : DiffUtil.ItemCallback<Event>() {
    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean =
        oldItem.event_title == newItem.event_title

    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean =
        oldItem == newItem
}
