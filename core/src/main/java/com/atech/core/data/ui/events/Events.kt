package com.atech.core.data.ui.events

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Events(
    val created: Long,
    val title: String,
    var content: String,
    val insta_link: String,
    val logo_link: String,
    val path: String,
    val society: String,
    val video_link: String,
) : Parcelable

class DiffUtilEvent : DiffUtil.ItemCallback<Events>() {
    override fun areItemsTheSame(oldItem: Events, newItem: Events): Boolean {
        return oldItem.created == newItem.created
    }

    override fun areContentsTheSame(oldItem: Events, newItem: Events): Boolean {
        return oldItem.content == newItem.content
    }
}