package com.atech.core.firebase.firestore

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
data class EventModel(
    val created: Long? = null,
    val title: String? = null,
    var content: String? = null,
    val insta_link: String? = null,
    val logo_link: String? = null,
    val path: String? = null,
    val society: String? = null,
    val video_link: String? = null,
    var attach: List<Attach>? = null,
)


@Keep
class NoticeModel(
    val title: String? = null,
    val body: String? = null,
    val link: String? = null,
    val sender: String? = null,
    val path: String? = null,
    val created: Long? = null,
)

@Keep
@Parcelize
data class Attach(
    var added: Long? = null,
    var link: String? = null
) : Parcelable




