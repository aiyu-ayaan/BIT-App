package com.atech.core.data.ui.events

import androidx.recyclerview.widget.DiffUtil

data class Events(
    val created: Long,
    val title: String,
    var content: String,
    val insta_link: String,
    val logo_link: String,
    val path: String,
    val poster_link: String,
    val society: String,
    val video_link: String,
)

class DiffUtilEvent : DiffUtil.ItemCallback<Events>() {
    override fun areItemsTheSame(oldItem: Events, newItem: Events): Boolean {
        return oldItem.created == newItem.created
    }

    override fun areContentsTheSame(oldItem: Events, newItem: Events): Boolean {
        return oldItem.content == newItem.content
    }
}