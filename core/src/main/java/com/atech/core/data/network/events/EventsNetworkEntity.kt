package com.atech.core.data.network.events

import androidx.annotation.Keep

@Keep
data class EventsNetworkEntity(
    val created: Long? = null,
    val title: String? = null,
    var content: String? = null,
    val insta_link: String? = null,
    val logo_link: String? = null,
    val path: String? = null,
    val society: String? = null,
    val video_link: String? = null,
)
